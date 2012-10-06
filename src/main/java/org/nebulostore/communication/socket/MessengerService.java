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

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.address.CommAddressResolver;
import org.nebulostore.communication.exceptions.AddressNotPresentException;
import org.nebulostore.communication.exceptions.CommException;
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
  private Boolean isEnding_ = false;

  public MessengerService(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue, CommAddressResolver resolver) {
    super(inQueue, outQueue);
    resolver_ = resolver;
    oosDispatcher_ = new CachedOOSDispatcher();
  }

  @Override
  public void endModule() {
    synchronized (isEnding_) {
      isEnding_ = true;
    }
    // Nothing to do here, because socket cache cleans up automatically after 1
    // second.
    super.endModule();
  }

  @Override
  public void processMessage(Message msg) {
    synchronized (isEnding_) {
      if (isEnding_) {
        logger_.warn("Can not process message, because commPeer is " +
            "shutting down.");
        return;
      }
    }
    if (!(msg instanceof CommMessage)) {
      logger_.error("Don't know what to do with message: " + msg);
      return;
    }
    CommMessage commMsg = (CommMessage) msg;
    if (commMsg.getSourceAddress() == null) {
      logger_.debug("Source address set to null, changing to my address.");
      commMsg.setSourceAddress(resolver_.getMyCommAddress());
    }
    logger_.info("Sending msg: " + commMsg + " to: " + commMsg.getDestinationAddress());
    try {
      ObjectOutputStream oos = oosDispatcher_.get(commMsg.getDestinationAddress());
      oos.writeObject(commMsg);
      logger_.debug("Message: " + commMsg + " sent to: " + commMsg.getDestinationAddress());
    } catch (IOException e) {
      logger_.error("IOException when trying to send: " + msg + ", to: " +
          commMsg.getDestinationAddress() + " " + e);
      outQueue_.add(new ErrorCommMessage((CommMessage) msg, new CommException(
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
   * For now it only handles one socket in use at a time, but it should be easy to
   * extend. It should the first thing too look into when it is found that
   * communication is too slow.
   *
   * @author Grzegorz Milka
   */
  //NOTE-GM: Can't find good name for this class. It should stress the fact that
  //it is producing sockets, and managing them and expects them to be returned.
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
              logger_.trace("Error when closing socket");
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
      Timer socketCleaner = new Timer();
      socketCleaner.schedule(new SocketCleaner(), CLEAN_TIMER, CLEAN_TIMER);
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
            //TODO(grzegorzmilka): Can we group those exceptions?
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
            if (socket != null) {
              socket.close();
            }
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
  }
}
