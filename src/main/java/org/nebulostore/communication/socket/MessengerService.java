package org.nebulostore.communication.socket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.EndModuleMessage;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.address.CommAddressResolver;
import org.nebulostore.communication.exceptions.AddressNotPresentException;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;


/**
 * Simple sender module.
 * Sends messages given to it by CommunicationPeer. If source address is null it
 * sets it to current one.
 *
 * @author Grzegorz Milka
 */
public class MessengerService extends Module {
  private static Logger logger_ = Logger.getLogger(MessengerService.class);
  private CommAddressResolver resolver_;
  private CachedOOSDispatcher oosDispatcher_;
  private ExecutorService service_ = Executors.newCachedThreadPool();
  private AtomicBoolean isEnding_ = new AtomicBoolean(false);

  public MessengerService(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue, CommAddressResolver resolver) {
    super(inQueue, outQueue);
    resolver_ = resolver;
    oosDispatcher_ = new CachedOOSDispatcher();
  }

  private void shutdown() {
    isEnding_.set(true);
    service_.shutdown();
    oosDispatcher_.shutdown();
    endModule();
  }

  @Override
  public void processMessage(Message msg) {
    if (isEnding_.get()) {
      logger_.warn("Can not process message, because commPeer is " +
          "shutting down.");
      return;
    }
    if (msg instanceof EndModuleMessage) {
      logger_.info("Received EndModule message");
      shutdown();
      return;
    } else if (!(msg instanceof CommMessage)) {
      logger_.error("Don't know what to do with message: " + msg);
      return;
    }
    CommMessage commMsg = (CommMessage) msg;
    if (commMsg.getSourceAddress() == null) {
      logger_.debug("Source address set to null, changing to my address.");
      commMsg.setSourceAddress(resolver_.getMyCommAddress());
    }
    logger_.debug("Sending msg: " + commMsg + " of class: " +
        commMsg.getClass().getName() + " to: " +
        commMsg.getDestinationAddress());

    service_.execute(new MessageSender(commMsg));
  }

  /**
   * Factory/Dispatcher for sockets.
   * It makes sockets to given CommAddresses and caches them for given CLEAN_TIME
   * interval. After every CLEAN_TIME interval it closes unused sockets.
   * It was introduced to deal with large number of messages to one host causing
   * depletion of ephemeral ports.
   *
   * Using is based on getting a socket for use through get method and returning
   * it through put method when it is not needed. A socket that is received from
   * get() method MUST be returned through put() method. Otherwise it will never
   * be cleaned and therefore it will waste resources on local and probably
   * remote machine.
   *
   * Inner working:
   * Cache uses three disjoint sets of sockets:
   * oldSockets - sockets that are not used and are to be cleaned during next
   *    round of cache cleaning
   * ruSockets - recently used sockets, sockets that have been used since last
   *    clean up and therefore are not to be cleaned during the next one. However
   *    they will go to oldSockets during next clean up.
   * activeSockets - sockets that are currently used. That is they were
   *    created/given through get() method and has not been put() back. Such
   *    sockets are never cleaned or moved to other sets while being used.
   *
   * @author Grzegorz Milka
   */
  private class CachedOOSDispatcher {
    /**
     * Structure for combining socket and ObjectOutputStream with it.
     * ObjectOutputStream follows a semantics of sending some kind of
     * initializing message when opening it. Therefore we can't recreate it
     * every time we want without doing same on the receiver side (or doing some
     * tricks). It is cheaper to simply let it be.
     * @author Grzegorz Milka
     */
    private class SocketOOSPair {
      public Socket socket_;
      public ObjectOutputStream oos_;
      public SocketOOSPair(Socket newSocket, ObjectOutputStream newOos) {
        socket_ = newSocket;
        oos_ = newOos;
      }
    }
    // Using ReentrantLock instead of Lock to be able to access hasWaiters
    // method
    private final ReentrantLock socketsLock_ = new ReentrantLock();
    /**
     * Conditions when waiting for given socket to be released.
     */
    private final Map<CommAddress, Condition> socketConds_ =
      Collections.synchronizedMap(new HashMap<CommAddress, Condition>());
    private final Map<CommAddress, SocketOOSPair> oldSockets_;
    private final Map<CommAddress, SocketOOSPair> ruSockets_;
    private final Map<CommAddress, SocketOOSPair> activeSockets_;
    /* 1 second */
    private static final int CLEAN_TIMER = 1000;
    private Timer socketCleaner_;

    /**
     * @author Grzegorz Milka
     */
    private class SocketCleaner extends TimerTask {
      @Override
      public void run() {
        logger_.trace("SocketCleaner cleaning");
        socketsLock_.lock();
        try {
          clearAndCloseSocketMap(oldSockets_);
          oldSockets_.putAll(ruSockets_);
          ruSockets_.clear();
        } finally {
          socketsLock_.unlock();
        }
      }
    }

    public CachedOOSDispatcher() {
      oldSockets_ = Collections.synchronizedMap(
          new HashMap<CommAddress, SocketOOSPair>());
      ruSockets_ = Collections.synchronizedMap(
          new HashMap<CommAddress, SocketOOSPair>());
      activeSockets_ = Collections.synchronizedMap(
          new HashMap<CommAddress, SocketOOSPair>());
      /* Set Timer to use a daemon thread */
      socketCleaner_ = new Timer(true);
      socketCleaner_.schedule(new SocketCleaner(), CLEAN_TIMER, CLEAN_TIMER);
    }

    /**
     * Returns ObjectOutputStream associated with a socket to given commAddress.
     *
     * If given socket is still present then it returns provided it is not used.
     * Otherwise the given socket is created and the returned.
     * Note that this method blocks in case socket to given commAddress is used
     * and waits till it is freed. It throws InterruptedException if interrupt
     * happens at this stage.
     *
     * This method always returns valid OOS unless it throws an exception.
     */
    public ObjectOutputStream get(CommAddress commAddress) throws IOException,
           InterruptedException {
      assert commAddress != null;
      socketsLock_.lock();
      try {
        if (activeSockets_.containsKey(commAddress)) {
          /* Wait till socket is free to use */
          Condition cond = socketConds_.get(commAddress);
          if (cond == null) {
            cond = socketsLock_.newCondition();
            socketConds_.put(commAddress, cond);
          }

          while (activeSockets_.containsKey(commAddress)) {
            cond.await();
          }

          if (!socketsLock_.hasWaiters(cond)) {
            /* Clear condition if nobody is waiting */
            socketConds_.remove(commAddress);
          }
        }

        Map<CommAddress, SocketOOSPair> containMap = null;
        if (oldSockets_.containsKey(commAddress)) {
          containMap = oldSockets_;
        } else if (ruSockets_.containsKey(commAddress)) {
          containMap = ruSockets_;
        }

        if (containMap != null) {
          activeSockets_.put(commAddress, containMap.get(commAddress));
          containMap.remove(commAddress);
        } else {
          assert !activeSockets_.containsKey(commAddress);
          /* Create socket */
          Socket socket = createSocket(commAddress);
          activeSockets_.put(commAddress, new SocketOOSPair(socket,
                new ObjectOutputStream(socket.getOutputStream())));
        }

        /* If we are here then activeSockets_ should contain sought socket */
        assert activeSockets_.containsKey(commAddress);
        return activeSockets_.get(commAddress).oos_;
      } finally {
        socketsLock_.unlock();
      }
    }

    public void put(CommAddress commAddress) {
      socketsLock_.lock();
      try {
        assert activeSockets_.containsKey(commAddress);
        ruSockets_.put(commAddress, activeSockets_.get(commAddress));
        activeSockets_.remove(commAddress);
        if (socketConds_.containsKey(commAddress)) {
          socketConds_.get(commAddress).signal();
        }
      } finally {
        socketsLock_.unlock();
      }
    }

    public void shutdown() {
      logger_.debug("Shutting down: " + this);
      socketCleaner_.cancel();
      socketsLock_.lock();
      try {
        clearAndCloseSocketMap(oldSockets_);
        clearAndCloseSocketMap(ruSockets_);
        clearAndCloseSocketMap(activeSockets_);
      } finally {
        socketsLock_.unlock();
      }
    }

    /**
     * Creates socket to host pointed by commAddress.
     */
    private Socket createSocket(CommAddress commAddress)
      throws IOException {
      /* Create socket */
      Socket socket = null;
      try {
        InetSocketAddress socketAddress = resolver_.resolve(commAddress);
        socket = new Socket(socketAddress.getAddress(), socketAddress.getPort());
      } catch (IOException e) {
        /* Socket is null so no need to close it */
        logger_.warn("Socket to: " + commAddress +
            " could not be created. " + e);
        throw new IOException("Socket to: " + commAddress +
            " could not be created.", e);
      } catch (AddressNotPresentException e) {
        logger_.warn("Socket to: " + commAddress +
            " could not be created. " + e);
        throw new IOException("Socket to: " + commAddress +
            " could not be created.", e);
      }
      logger_.debug("Socket to: " + commAddress + " created.");
      return socket;
    }

    /**
     * Closes sockets in given socket-OOS Map.
     *
     * Assumes we have appriopiate locks to manipulate that map.
     * @author Grzegorz Milka
     */
    private void clearAndCloseSocketMap(Map<CommAddress, SocketOOSPair> sockMap) {
      for (SocketOOSPair pair : sockMap.values()) {
        Socket socket = pair.socket_;
        try {
          socket.close();
          logger_.debug("Socket to: " + socket.getRemoteSocketAddress() +
              " closed.");
        } catch (IOException e) {
          logger_.debug("Error when closing socket");
        }
      }
      sockMap.clear();
    }
  }

  /**
   * Simple runnable which handles sending CommMessage over network.
   */
  private class MessageSender implements Runnable {
    private final CommMessage commMsg_;
    public MessageSender(CommMessage msg) {
      commMsg_ = msg;
    }

    @Override
    public void run() {
      ObjectOutputStream oos = null;
      try {
        oos = oosDispatcher_.get(commMsg_.getDestinationAddress());
      } catch (IOException e) {
        logger_.warn("IOException when getting socket to: " + commMsg_ +
            ", to: " + commMsg_.getDestinationAddress() + " " + e);
        outQueue_.add(new ErrorCommMessage(commMsg_, new NebuloException(
                "Message " + commMsg_ + " couldn't be sent.")));
        return;
      } catch (InterruptedException e) {
        logger_.warn("Interrupt when getting socket to: " + commMsg_ +
            ", to: " + commMsg_.getDestinationAddress() + " " + e);
        outQueue_.add(new ErrorCommMessage(commMsg_, new NebuloException(
                "Sending of message: " + commMsg_ + ", was interrupted.")));
        return;
      }

      try {
        oos.writeObject(commMsg_);
        logger_.debug("Message: " + commMsg_ + " sent to: " +
            commMsg_.getDestinationAddress());
      } catch (IOException e) {
        logger_.warn("IOException when trying to send: " + commMsg_ + ", to: " +
            commMsg_.getDestinationAddress() + " " + e);
        outQueue_.add(new ErrorCommMessage(commMsg_, new NebuloException(
                "Message " + commMsg_ + " couldn't be sent.")));
      } finally {
        oosDispatcher_.put(commMsg_.getDestinationAddress());
      }
    }

  }
}
