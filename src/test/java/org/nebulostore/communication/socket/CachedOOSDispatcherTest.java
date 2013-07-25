package org.nebulostore.communication.socket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.socket.EndTestMessage;
import org.nebulostore.communication.socket.ManualResolver;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.socket.messages.ListenerServiceReadyMessage;

import static org.junit.Assert.assertTrue;

/**
 * Unit Test for CachedOOSDispatcher.
 *
 * @author Grzegorz Milka
 */
public class CachedOOSDispatcherTest {
  private static final CommAddress MY_COMM_ADDRESS = new CommAddress(0, 0);
  private static final int FIRST_LISTEN_PORT = 2010;
  private static final int NUM_LISTENERS = 8;
  private static final int NUM_SENDERS = 1000;
  private ManualResolver resolver_;
  private CachedOOSDispatcher coos_;

  @Before
  public void setUp() {
    resolver_ = new ManualResolver(MY_COMM_ADDRESS);
    coos_ = new CachedOOSDispatcher(resolver_);
  }

  @After
  public void tearDown() {
    coos_.shutdown();
  }

  /**
   * Runs NUM_LISTENERS independent listeners and send to them NUM_SENDERS messages.
   * All done using {@Link CachedOOSDispatcher}.
   *
   * @author Grzegorz Milka
   *
   * @throws IOException
   * @throws InterruptedException
   */
  @Test
  public void testGet() throws IOException, InterruptedException {
    Collection<Listener> listeners = new LinkedList<>();
    Collection<Thread> listenerThreads = new LinkedList<>();
    Collection<CommAddress> recipients = new LinkedList<>();
    Collection<Thread> senderThreads = new LinkedList<>();

    initializeListeners(listeners, listenerThreads, recipients, resolver_,
        NUM_LISTENERS);

    initializeSenders(senderThreads, recipients, coos_, NUM_SENDERS);

    for (Thread thr: senderThreads) {
      thr.start();
    }

    joinTestThreads(listenerThreads, senderThreads, coos_);

    /* Check results */
    for (Listener listener: listeners) {
      assertTrue(listener.isSuccess());
      assertTrue(listener.getCount() == NUM_SENDERS);
    }
  }

  /**
   * Runs CachedOOSDispatcher for nonexistent address and checks if it returns
   * exception.
   * @author Grzegorz Milka
   * @throws InterruptedException
   */
  @Test(expected = IOException.class)
  public void testGetException() throws IOException, InterruptedException {
    coos_.get(MY_COMM_ADDRESS);
  }

  private void initializeListeners(Collection<Listener> listeners,
      Collection<Thread> listenerThreads, Collection<CommAddress> recipients,
      ManualResolver resolver, int howMany) throws UnknownHostException {
    for (int i = 0; i < howMany; ++i) {
      CommAddress cA = new CommAddress(0, i + 1);
      Listener listener = new Listener(cA, FIRST_LISTEN_PORT + i);
      Thread listenerThread = new Thread(listener);
      listenerThread.setDaemon(true);
      listenerThread.start();
      listeners.add(listener);
      listenerThreads.add(listenerThread);
      recipients.add(cA);
      resolver.addMapping(cA, new InetSocketAddress(Inet4Address.getLocalHost(),
          FIRST_LISTEN_PORT + i));
    }
  }

  private void initializeSenders(Collection<Thread> senderThreads,
      Collection<CommAddress> recipients, CachedOOSDispatcher coos,
      int howMany) {
    for (int i = 0; i < howMany; ++i) {
      List<CommAddress> copyRecipients = new ArrayList<>(recipients);
      Collections.shuffle(copyRecipients);
      Sender sender = new Sender(coos, copyRecipients);
      Thread senderThread = new Thread(sender);
      senderThread.setDaemon(true);
      senderThreads.add(senderThread);
    }
  }

  private void joinTestThreads(Collection<Thread> listenerThreads,
      Collection<Thread> senderThreads, CachedOOSDispatcher coos)
    throws IOException, InterruptedException {

    for (Thread thr : senderThreads) {
      thr.join();
    }

    for (int i = 0; i < listenerThreads.size(); ++i) {
      ObjectOutputStream oos = coos.get(new CommAddress(0, i + 1));
      oos.writeObject(new EndTestMessage(MY_COMM_ADDRESS, new CommAddress(0, i + 1)));
      coos.put(new CommAddress(0, i + 1), oos);
    }

    coos.shutdown();

    for (Thread thread : listenerThreads) {
      thread.join();
    }
  }
  /**
   * Listens for messages using {@Link ListenerService} and checks their count
   * as well as correctness. This module is stopped by sending EndTestMessage.
   *
   * @author Grzegorz Milka
   */
  private static class Listener implements Runnable {
    private int listenPort_;
    private int count_;
    private boolean hasReceivedBadMsg_;
    private CommAddress myCA_;

    public Listener(CommAddress myCA, int listenPort) {
      myCA_ = myCA;
      listenPort_ = listenPort;
    }

    public int getCount() {
      return count_;
    }

    public boolean isSuccess() {
      return !hasReceivedBadMsg_;
    }

    public void run() {
      Message recvMsg = null;
      BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
      ListenerService listener = new ListenerService(queue, listenPort_);
      Thread listenerThread = new Thread(listener);
      listenerThread.setDaemon(true);
      listenerThread.start();

      while (!(recvMsg instanceof EndTestMessage)) {
        try {
          recvMsg = queue.take();
        } catch (InterruptedException e) {
          listener.shutdown();
          Thread.currentThread().interrupt();
          return;
        }
        if ((recvMsg instanceof CommPeerFoundMessage) &&
            ((CommMessage) recvMsg).getDestinationAddress().equals(myCA_)) {
          count_ += 1;
        } else if (!((recvMsg instanceof EndTestMessage) ||
            (recvMsg instanceof ListenerServiceReadyMessage))) {
          hasReceivedBadMsg_ = true;
        }
      }

      listener.shutdown();
      try {
        listenerThread.join();
      } catch (InterruptedException e) {
        return;
      }
    }
  }

  /**
   * Sends {@link HolderAdvertisementMessage} messages to given recipients.
   *
   * @author Grzegorz Milka
   */
  private static class Sender implements Runnable {
    private CachedOOSDispatcher coos_;
    private Collection<CommAddress> recipients_;

    public Sender(CachedOOSDispatcher coos, Collection<CommAddress> recipients) {
      coos_ = coos;
      recipients_ = recipients;
    }

    public void run() {
      for (CommAddress recipient: recipients_) {
        CommMessage commMsg = new CommPeerFoundMessage(MY_COMM_ADDRESS, recipient);
        ObjectOutputStream oos = null;
        try {
          oos = coos_.get(recipient);
          oos.writeObject(commMsg);
          oos.flush();
        } catch (IOException | InterruptedException e) {
          return;
        } finally {
          if (oos != null) {
            coos_.put(recipient, oos);
          }
        }
      }
    }
  }
}
