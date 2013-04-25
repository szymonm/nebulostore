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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.inject.Inject;

import org.apache.log4j.Logger;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.address.CommAddressResolver;
import org.nebulostore.communication.exceptions.AddressNotPresentException;

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
public class CachedOOSDispatcher implements OOSDispatcher {
  private static Logger logger_ = Logger.getLogger(CachedOOSDispatcher.class);
  /**
   * Structure for combining socket and ObjectOutputStream with it.
   * ObjectOutputStream follows a semantics of sending some kind of
   * initializing message when opening it. Therefore we can't recreate it
   * every time we want without doing same on the receiver side (or doing some
   * tricks). It is cheaper to simply let it be.
   * @author Grzegorz Milka
   */
  private static class SocketOOSPair {
    public Socket socket_;
    public ObjectOutputStream oos_;
    public SocketOOSPair(Socket newSocket, ObjectOutputStream newOos) {
      socket_ = newSocket;
      oos_ = newOos;
    }
  }

  /**
   * Lock for operations on *Sockets_ maps.
   */
  private final Lock socketsLock_ = new ReentrantLock();
  /**
   * Locks associated with given CommAddress.
   *
   * We allow only one connection to given host at the same time.
   */
  private final Map<CommAddress, Lock> socketLocksMap_ =
    Collections.synchronizedMap(new HashMap<CommAddress, Lock>());
  /**
   * Counts number of users of given CommAddress lock.
   */
  private final Map<CommAddress, Integer> socketLocksWaiters_ =
    Collections.synchronizedMap(new HashMap<CommAddress, Integer>());
  private final Map<CommAddress, SocketOOSPair> oldSockets_;
  private final Map<CommAddress, SocketOOSPair> ruSockets_;
  private final Map<CommAddress, SocketOOSPair> activeSockets_;
  /* 1 second */
  private static final int CLEAN_TIMER = 1000;
  private Timer socketCleaner_;
  private CommAddressResolver resolver_;

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

  @Inject
  public CachedOOSDispatcher(CommAddressResolver resolver) {
    oldSockets_ = Collections.synchronizedMap(
        new HashMap<CommAddress, SocketOOSPair>());
    ruSockets_ = Collections.synchronizedMap(
        new HashMap<CommAddress, SocketOOSPair>());
    activeSockets_ = Collections.synchronizedMap(
        new HashMap<CommAddress, SocketOOSPair>());
    /* Set Timer to use a daemon thread */
    socketCleaner_ = new Timer(true);
    socketCleaner_.schedule(new SocketCleaner(), CLEAN_TIMER, CLEAN_TIMER);
    resolver_ = resolver;
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
    boolean isSuccessful = false;
    Lock lock = getLock(commAddress);
    logger_.trace("Trying to lock commAddress's lock for: " + commAddress);
    lock.lock();
    try {
      logger_.trace("commAddress's lock for: " + commAddress + " locked");
      logger_.trace("Trying to lock socketsLock_");
      socketsLock_.lock();
      try {
        logger_.trace("socketsLock_ locked");
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
        }
      } finally {
        socketsLock_.unlock();
      }

      Socket socket = null;
      if (!activeSockets_.containsKey(commAddress)) {
        socket = createSocket(commAddress);
      }

      ObjectOutputStream commOos;
      socketsLock_.lock();
      try {
        if (socket != null) {
          activeSockets_.put(commAddress, new SocketOOSPair(socket,
              new ObjectOutputStream(socket.getOutputStream())));
        }
        commOos = activeSockets_.get(commAddress).oos_;
      } finally {
        socketsLock_.unlock();
      }

      isSuccessful = true;
      return commOos;
    } finally {
      if (!isSuccessful) {
        lock.unlock();
        logger_.trace("commAddress's lock for: " + commAddress + " released");
      }
    }
  }

  public void put(CommAddress commAddress, ObjectOutputStream oos) {
    socketsLock_.lock();
    try {
      ruSockets_.put(commAddress, activeSockets_.get(commAddress));
      activeSockets_.remove(commAddress);
    } finally {
      socketsLock_.unlock();
      Lock lock = socketLocksMap_.get(commAddress);
      releaseLock(commAddress);
      lock.unlock();
    }
  }

  /**
   * Returns lock object for operations to given commAddress.
   *
   * Each getLock should be followed by releaseLock after operations are done.
   * Note: getLock and releaseLock do not lock or unlock the lock in question.
   */
  private Lock getLock(CommAddress commAddress) {
    socketsLock_.lock();
    try {
      Lock lock = socketLocksMap_.get(commAddress);
      if (lock == null) {
        lock = new ReentrantLock();
        socketLocksMap_.put(commAddress, lock);
        socketLocksWaiters_.put(commAddress, 0);
      }
      int waiters = socketLocksWaiters_.get(commAddress);
      socketLocksWaiters_.put(commAddress, waiters + 1);
      return lock;
    } finally {
      socketsLock_.unlock();
    }
  }

  /**
   * Decreases the usage count for given lock and removes it from map if no
   * one else has it.
   */
  private void releaseLock(CommAddress commAddress) {
    socketsLock_.lock();
    try {
      Lock lock = socketLocksMap_.get(commAddress);
      int waiters = socketLocksWaiters_.get(commAddress);
      if (waiters == 1) {
        socketLocksMap_.remove(commAddress);
        socketLocksWaiters_.remove(commAddress);
      } else {
        socketLocksWaiters_.put(commAddress, waiters - 1);
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
      logger_.trace("Creating socket to: " + socketAddress);
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
   * Assumes we have appriopiate locks to manipulate that map or at least no one
   * else uses those maps.
   * @author Grzegorz Milka
   */
  private void clearAndCloseSocketMap(Map<CommAddress, SocketOOSPair> sockMap) {
    for (SocketOOSPair pair : sockMap.values()) {
      Socket socket = pair.socket_;
      ObjectOutputStream oos = pair.oos_;
      try {
        oos.close();
        socket.close();
        logger_.debug("Socket to: " + socket.getRemoteSocketAddress() +
            " closed.");
      } catch (IOException e) {
        logger_.debug("Error when closing socket: " + e);
      }
    }
    sockMap.clear();
  }
}
