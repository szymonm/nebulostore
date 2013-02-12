package org.nebulostore.communication.socket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

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
  private AtomicBoolean isEnding_ = new AtomicBoolean(false);

  public MessengerService(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue, CommAddressResolver resolver) {
    super(inQueue, outQueue);
    resolver_ = resolver;
    oosDispatcher_ = new CachedOOSDispatcher();
  }

  @Override
  protected void endModule() {
    isEnding_.set(true);
    // Nothing to do here, because socket cache cleans up automatically after 1
    // second.
    oosDispatcher_.shutdown();
    super.endModule();
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
      endModule();
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
    logger_.info("Sending msg: " + commMsg + " of class: " + commMsg.getClass().getName() +
        " to: " + commMsg.getDestinationAddress());
    try {
      ObjectOutputStream oos = oosDispatcher_.get(commMsg.getDestinationAddress());
      oos.writeObject(commMsg);
      logger_.debug("Message: " + commMsg + " sent to: " + commMsg.getDestinationAddress());
    } catch (IOException e) {
      logger_.error("IOException when trying to send: " + msg + ", to: " +
          commMsg.getDestinationAddress() + " " + e);
      outQueue_.add(new ErrorCommMessage((CommMessage) msg, new NebuloException(
              "Message " + msg + " couldn't be sent.")));
    } finally {
      oosDispatcher_.put();
    }
  }

  /**
   * Factory/Dispatcher for sockets.
   * It makes sockets to given CommAddresses and cashes them for given CLEAN_TIME
   * interval. After every CLEAN_TIME interval it closes unused sockets.
   * It was introduced to deal with large number of messages to one host causing
   * depletion of ephemeral hosts. NOTE-GM this behaviour is not present now.
   *
   * Using is based on getting a socket for use and returning it through put
   * method when it is not needed;
   * For now it only handles one socket in use at a time, but it should be easy
   * to extend. It should the first thing too look into when it is found that
   * communication is too slow.
   *
   * @author Grzegorz Milka
   */
  private class CachedOOSDispatcher {
    /**
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

    private Map<CommAddress, SocketOOSPair> cacheMap_;
    private CommAddress activeAddress_;
    private SocketOOSPair activeSOOSPair_;
    // 1 second
    private static final int CLEAN_TIMER = 1000;
    private Timer socketCleaner_;

    /**
     * @author Grzegorz Milka
     */
    private class SocketCleaner extends TimerTask {
      @Override
      public void run() {
        logger_.trace("SocketCleaner cleaning");
        synchronized (cacheMap_) {
          for (SocketOOSPair pair : cacheMap_.values()) {
            Socket socket = pair.socket_;
            try {
              socket.close();
              logger_.debug("Socket to: " + socket.getRemoteSocketAddress() + " closed.");
            } catch (IOException e) {
              logger_.debug("Error when closing socket");
            }
          }
          cacheMap_.clear();
        }
      }
    }

    public CachedOOSDispatcher() {
      cacheMap_ = new HashMap<CommAddress, SocketOOSPair>();
      activeAddress_ = null;
      activeSOOSPair_ = null;
      /* Set Timer to use a daemon thread */
      socketCleaner_ = new Timer(true);
      socketCleaner_.schedule(new SocketCleaner(), CLEAN_TIMER, CLEAN_TIMER);
    }

    public ObjectOutputStream get(CommAddress commAddress) throws IOException {
      assert commAddress != null;
      synchronized (cacheMap_) {
        assert activeAddress_ == null && activeSOOSPair_ == null;
        if (cacheMap_.containsKey(commAddress)) {
          logger_.trace("Socket to: " + commAddress + " exists.");
          activeAddress_ = commAddress;
          activeSOOSPair_ = cacheMap_.get(commAddress);
          cacheMap_.remove(commAddress);
        } else {
          Socket socket = null;
          try {
            InetSocketAddress socketAddress = resolver_.resolve(commAddress);
            socket = new Socket(socketAddress.getAddress(), socketAddress.getPort());
            activeSOOSPair_ = new SocketOOSPair(socket,
                new ObjectOutputStream(socket.getOutputStream()));
          } catch (IOException e) {
            logger_.error("Socket to: " + commAddress +
                " could not be created. " + e);
            activeSOOSPair_ = null;
            if (socket != null) {
              socket.close();
            }
            throw new IOException("Socket to: " + commAddress +
                " could not be created.", e);
          } catch (AddressNotPresentException e) {
            //NOTE-GM We could differentiate between those exception and based
            //on this decide whether to remove host from pool or not.
            logger_.error("Socket to: " + commAddress +
                " could not be created. " + e);
            activeSOOSPair_ = null;
            throw new IOException("Socket to: " + commAddress +
                " could not be created.", e);
          }
          logger_.debug("Socket to: " + commAddress + " created.");
          activeAddress_ = commAddress;
        }
        return activeSOOSPair_.oos_;
      }
    }

    public void put() {
      synchronized (cacheMap_) {
        assert activeAddress_ != null && activeSOOSPair_ != null;
        cacheMap_.put(activeAddress_, activeSOOSPair_);
        activeAddress_ = null;
        activeSOOSPair_ = null;
      }
    }

    public void shutdown() {
      logger_.debug("Shutting down: " + this);
      socketCleaner_.cancel();
      synchronized (cacheMap_) {
        for (SocketOOSPair pair : cacheMap_.values()) {
          Socket socket = pair.socket_;
          try {
            socket.close();
            logger_.debug("Socket to: " + socket.getRemoteSocketAddress() +
                " closed.");
          } catch (IOException e) {
            logger_.debug("Error when closing socket");
          }
        }
        cacheMap_.clear();
      }
    }
  }
}
