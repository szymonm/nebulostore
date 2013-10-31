package org.nebulostore.communication.socket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.socket.messages.ListenerServiceReadyMessage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Grzegorz Milka
 */
public class ListenerServiceTest {
  private static final int LISTENING_PORT = 20000;
  private static final int THREAD_JOIN_WAIT = 2000;
  private static final int TEST_TIMEOUT = 5000;
  private LinkedBlockingQueue<Message> inQueue_;
  private ListenerService listener_;
  private Thread listenerThread_;

  @Before
  public void setUp() throws InterruptedException {
    inQueue_ = new LinkedBlockingQueue<>();
    listener_ = new ListenerService(inQueue_, LISTENING_PORT);
    listenerThread_ = new Thread(listener_);
    listenerThread_.start();
    Message readyMsg = inQueue_.take();
    assertTrue(readyMsg instanceof ListenerServiceReadyMessage);
  }

  @After
  public void tearDown() throws InterruptedException {
    listener_.shutdown();
    listenerThread_.join(THREAD_JOIN_WAIT);
    assertFalse(listenerThread_.isAlive());
  }

  /**
   * A simple check whether listener service listens on given port and
   * reports back messages.
   *
   * @throws IOException
   */
  @Test(timeout = TEST_TIMEOUT)
  public void testReceiveMessage() throws IOException, InterruptedException {
    Socket socket = new Socket(Inet4Address.getLocalHost(), LISTENING_PORT);
    CommAddress src = new CommAddress(0, 0);
    CommAddress dest = new CommAddress(0, 1);
    CommMessage commMsg = new CommPeerFoundMessage(src, dest);

    try {
      ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
      oos.writeObject(commMsg);
      oos.close();
    } finally {
      socket.close();
    }

    Message msg = inQueue_.take();
    assertTrue(msg instanceof CommPeerFoundMessage);
    CommPeerFoundMessage cPFM = (CommPeerFoundMessage) msg;
    assertTrue(cPFM.getDestinationAddress().equals(dest));
    assertTrue(cPFM.getSourceAddress().equals(src));
  }
}
