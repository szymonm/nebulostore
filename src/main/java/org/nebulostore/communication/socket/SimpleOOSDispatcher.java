package org.nebulostore.communication.socket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import com.google.inject.Inject;

import org.apache.log4j.Logger;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.address.CommAddressResolver;
import org.nebulostore.communication.exceptions.AddressNotPresentException;

/**
 * @author Bolek Kulbabinski
 */
public class SimpleOOSDispatcher implements OOSDispatcher {
  private static Logger logger_ = Logger.getLogger(SimpleOOSDispatcher.class);
  /**
   * @author Grzegorz Milka
   */
  protected static class SocketOOSPair {
    public Socket socket_;
    public ObjectOutputStream oos_;
    public int counter_;
    public SocketOOSPair(Socket newSocket, ObjectOutputStream newOos) {
      socket_ = newSocket;
      oos_ = newOos;
      counter_ = 0;
    }
  }

  /**
   * @author Grzegorz Milka
   */
  protected class SocketCleaner extends TimerTask {
    @Override
    public void run() {
      logger_.trace("SocketCleaner cleaning");
      boolean unused = true;
      while (unused) {
        unused = false;
        socketsLock_.acquireUninterruptibly();
        CommAddress unAddr = null;
        SocketOOSPair unPair = null;
        for (Entry<CommAddress, SocketOOSPair> entry : sockets_.entrySet()) {
          if (entry.getValue().counter_ == 0) {
            unAddr = entry.getKey();
            unPair = entry.getValue();
            break;
          }
        }
        if (unPair != null) {
          unused = true;
          closeSocket(unPair.socket_);
          sockets_.remove(unAddr);
        }
        socketsLock_.release();
      }
    }
  }

  private final Semaphore socketsLock_ = new Semaphore(1, true);
  private final Map<CommAddress, Semaphore> socketSems_ = new HashMap<CommAddress, Semaphore>();
  private final Map<CommAddress, SocketOOSPair> sockets_ =
      new HashMap<CommAddress, SocketOOSPair>();

  private static final int CLEAN_TIMER = 5 * 1000;
  private Timer socketCleaner_;
  private final CommAddressResolver resolver_;

  @Inject
  public SimpleOOSDispatcher(CommAddressResolver resolver) {
    resolver_ = resolver;
    /* Set Timer to use a daemon thread */
    socketCleaner_ = new Timer(true);
    //socketCleaner_.schedule(new SocketCleaner(), CLEAN_TIMER, CLEAN_TIMER);
  }

  public ObjectOutputStream get(CommAddress commAddress)
    throws IOException, InterruptedException {

    SocketOOSPair pair = null;
    Semaphore sem = null;
    socketsLock_.acquireUninterruptibly();
    if (socketSems_.containsKey(commAddress)) {
      pair = sockets_.get(commAddress);
      sem = socketSems_.get(commAddress);
    } else {
      pair = new SocketOOSPair(null, null);
      sockets_.put(commAddress, pair);
      ++pair.counter_;
      sem = new Semaphore(1);
      socketSems_.put(commAddress, sem);
    }
    socketsLock_.release();

    sem.acquireUninterruptibly();
    if (pair.socket_ == null) {
      pair.socket_ = createSocket(commAddress);
      pair.oos_ = new ObjectOutputStream(pair.socket_.getOutputStream());
    }

    return pair.oos_;
  }

  public void put(CommAddress commAddress, ObjectOutputStream oos) {
    SocketOOSPair pair = null;
    Semaphore sem = null;
    socketsLock_.acquireUninterruptibly();
    pair = sockets_.get(commAddress);
    --pair.counter_;
    sem = socketSems_.get(commAddress);
    socketsLock_.release();
    sem.release();
  }

  public void shutdown() {
    logger_.debug("Shutting down: " + this);
    socketCleaner_.cancel();
    socketsLock_.acquireUninterruptibly();
    clearAndCloseSocketMap(sockets_);
    socketsLock_.release();
  }

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
      //throw new IOException("Socket to: " + commAddress +
        //  " could not be created.", e);
    } catch (AddressNotPresentException e) {
      logger_.warn("Socket to: " + commAddress +
          " could not be created. " + e);
    }
    logger_.debug("Socket to: " + commAddress + " created.");
    return socket;
  }

  private void clearAndCloseSocketMap(Map<CommAddress, SocketOOSPair> sockMap) {
    for (SocketOOSPair pair : sockMap.values()) {
      Socket socket = pair.socket_;
      closeSocket(socket);
    }
    sockMap.clear();
  }

  private void closeSocket(Socket socket) {
    try {
      socket.close();
      logger_.debug("Socket to: " + socket.getRemoteSocketAddress() +
          " closed.");
    } catch (IOException e) {
      logger_.debug("Error when closing socket");
    }
  }
}
