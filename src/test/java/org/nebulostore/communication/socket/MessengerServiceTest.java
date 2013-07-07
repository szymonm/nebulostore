package org.nebulostore.communication.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.modules.EndModuleMessage;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.messages.HolderAdvertisementMessage;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Grzegorz Milka
 */
public class MessengerServiceTest {
  private static final int MY_LISTENING_PORT = 20002;
  private static final int DEST_LISTENING_PORT = 20004;
  private static final int THREAD_JOIN_WAIT = 2000;
  private static final int RECV_MSG_WAIT = 2000;
  private static final CommAddress MY_COMM_ADDRESS = new CommAddress(0, 0);
  private static final CommAddress DEST_COMM_ADDRESS = new CommAddress(0, 1);
  private BlockingQueue<Message> inQueue_;
  private BlockingQueue<Message> outQueue_;
  private ManualResolver resolver_;
  private MessengerService messenger_;
  private Thread messThread_;

  @Before
  public void setUp() {
    inQueue_ = new LinkedBlockingQueue<Message>();
    outQueue_ = new LinkedBlockingQueue<>();
    resolver_ = new ManualResolver(MY_COMM_ADDRESS);
    messenger_ = new MessengerService(inQueue_, outQueue_, resolver_);
    messThread_ = new Thread(messenger_);
    messThread_.setDaemon(true);
    messThread_.start();
  }

  @After
  public void tearDown() throws Exception {
    inQueue_.add(new EndModuleMessage());
    messThread_.join(THREAD_JOIN_WAIT);
    assertFalse(messThread_.isAlive());
  }

  /**
   * Sends message to self and checks if it is really sent.
   *
   * @throws IOException
   */
  @Test
  public void testSendMessageToSelf() throws IOException, InterruptedException {
    resolver_.addMapping(MY_COMM_ADDRESS,
        new InetSocketAddress(Inet4Address.getLocalHost(), MY_LISTENING_PORT));
    testSendMessage(MY_COMM_ADDRESS, MY_LISTENING_PORT);
  }

  /**
   * Sends message to nonexistent host and checks for error message.
   *
   * @throws IOException
   * @throws InterruptedException
   */
  @Test
  public void testSendMessageToNonexistentHost()
    throws IOException, InterruptedException {
    CommMessage commMsg = new HolderAdvertisementMessage(MY_COMM_ADDRESS, DEST_COMM_ADDRESS);
    Message recvMsg;

    inQueue_.add(commMsg);
    recvMsg = outQueue_.take();
    assertTrue(recvMsg instanceof ErrorCommMessage);
    assertTrue(commMsg.equals(((ErrorCommMessage) recvMsg).getMessage()));
  }

  /**
   * Sends message to address other than self and checks if it is really sent.
   *
   * @throws IOException
   * @throws InterruptedException
   */
  @Test
  public void testSendMessageToDest() throws IOException, InterruptedException {
    resolver_.addMapping(DEST_COMM_ADDRESS,
        new InetSocketAddress(Inet4Address.getLocalHost(), DEST_LISTENING_PORT));
    testSendMessage(DEST_COMM_ADDRESS, DEST_LISTENING_PORT);
  }

  /*
   * Sends message to given destination {@Link CommAddress} and listens for
   * it on given destPort port number.
   */
  private void testSendMessage(CommAddress destCA, int destPort)
    throws IOException, InterruptedException {
    /* Throwing IOException to from function to get cleaner code */
    CommMessage commMsg = new HolderAdvertisementMessage(MY_COMM_ADDRESS, destCA);
    Exchanger<Message> exchanger = new Exchanger<>();
    Message recvMsg;
    try (ServerSocket server = new ServerSocket(destPort)) {
      ListenerProtocol listener = new ListenerProtocol(server, exchanger);
      Thread listenThread = new Thread(listener);
      listenThread.setDaemon(true);
      listenThread.start();

      inQueue_.add(commMsg);

      try {
        recvMsg = exchanger.exchange(null, RECV_MSG_WAIT, TimeUnit.MILLISECONDS);
      } catch (TimeoutException e) {
        listenThread.interrupt();
        listenThread.join(THREAD_JOIN_WAIT);
        fail("Listening thread didn't end in specified amount of time.");
        return;
      }

      listenThread.join();

      if (recvMsg == null) {
        fail("Listener failed with exception: " + listener.getException());
      }
    }

    assertTrue(commMsg.equals(recvMsg));
  }


  /**
   * Handler for incoming connection. It listens for one message and puts it into exchanger. On
   * error the message is null and exception is available through getException;
   *
   * @author Grzegorz Milka
   */
  private class ListenerProtocol implements Runnable {
    private ServerSocket server_;
    private Message msg_;
    private Exception exception_;
    private Exchanger<Message> exchanger_;

    public ListenerProtocol(ServerSocket server, Exchanger<Message> exchanger) {
      server_ = server;
      exchanger_ = exchanger;
    }

    public Exception getException() {
      return exception_;
    }

    public void run() {
      try (Socket clientSocket = server_.accept()) {
        InputStream sis = clientSocket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(sis);
        msg_ = (Message) ois.readObject();
      } catch (ClassNotFoundException | IOException e) {
        msg_ = null;
        exception_ = e;
      }

      try {
        exchanger_.exchange(msg_);
      } catch (InterruptedException e) {
        /* Allow thread to exit */
        return;
      }
    }
  }
}
