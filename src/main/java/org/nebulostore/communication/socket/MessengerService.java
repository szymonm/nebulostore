package org.nebulostore.communication.socket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.exceptions.CommException;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;


/**
 * Module for sending CommMessages through UDP.
 * It uses simple timeout-interlock mechanism for confirming delivery.
 * @author Grzegorz Milka
 */
public class MessengerService extends Module {

  /**
   * Factory/Dispatcher for sockets. 
   * It makes sockets to given CommAddresses and cashes them for given CLEAN_TIME
   * interval. After every CLEAN_TIME interval it closes unused sockets.
   * It was introduced to deal with large number of messages to one host causing
   * depletion of ephemeral hosts.
   *
   * Using is based on getting a socket for use and returning it through put
   * method when it is not needed;
   * For now it only handles one socket in use at a time, but it should be easy to
   * extend.
   */
  //NOTE-GM: Can't find good name for this class. It should stress the fact that
  //it is producing sockets, and managing them and expects them to be returned.
  private class CachedOOSDispatcher {
    private class SocketOOSPair {
      public Socket socket;
      public ObjectOutputStream oos;
      public SocketOOSPair(Socket newSocket, ObjectOutputStream newOos) {
        socket = newSocket;
        oos = newOos;
      }
    }

    private Map<CommAddress, SocketOOSPair> cacheMap_;
    private CommAddress activeAddress_;
    private SocketOOSPair activeSOOSPair_;
    private int CLEAN_TIMER = 1000; // 1 second

    private class SocketCleaner extends TimerTask {
      @Override
      public void run() {
        logger_.trace("SocketCleaner cleaning");
        synchronized(cacheMap_) {
          for(SocketOOSPair pair: cacheMap_.values()) {
            Socket socket = pair.socket;
            try {
              socket.close();
              logger_.debug("Socket to: " + socket.getRemoteSocketAddress() + " closed.");
            } catch (IOException e){
              logger_.trace("Error when closing socket");
            }
          }
          cacheMap_.clear();
        }
      }
    }

    public CachedOOSDispatcher(){
      cacheMap_ = new HashMap<CommAddress, SocketOOSPair>();
      activeAddress_ = null;
      activeSOOSPair_ = null;
      Timer socketCleaner = new Timer();
      socketCleaner.schedule(new SocketCleaner(), CLEAN_TIMER, CLEAN_TIMER);
    }

    public ObjectOutputStream get(CommAddress commAddress) throws IOException {

      assert commAddress != null;
      synchronized(cacheMap_) {
        assert activeAddress_ == null && activeSOOSPair_ == null;
        if(cacheMap_.containsKey(commAddress)) {
          logger_.trace("Socket to: " + commAddress + " exists.");
          activeAddress_ = commAddress;
          activeSOOSPair_ = cacheMap_.get(commAddress);
          cacheMap_.remove(commAddress);
        } else {
          Socket socket = null;
          try {
            socket = new Socket(
                commAddress.getAddress().getAddress(),
                commAddress.getAddress().getPort());
            activeSOOSPair_ = new SocketOOSPair(socket, 
                new ObjectOutputStream(socket.getOutputStream()));
          } catch (IOException e) {
            logger_.error("Socket to: " + commAddress + 
                " could not be created. " + e);
            activeSOOSPair_ = null;
            if(socket != null) {
              socket.close();
            }
            throw new IOException("Socket to: " + commAddress + 
                " could not be created.", e);
          }
          logger_.debug("Socket to: " + commAddress + " created.");
          activeAddress_ = commAddress;
        }
        return activeSOOSPair_.oos;
      }
    }

    public void put() {
      synchronized(cacheMap_) {
        assert activeAddress_ != null && activeSOOSPair_ != null;
        cacheMap_.put(activeAddress_, activeSOOSPair_);
        activeAddress_ = null;
        activeSOOSPair_ = null;
      }
    }
  }

  private static Logger logger_ = Logger.getLogger(MessengerService.class);
  private CommAddress myAddress_;
  private CachedOOSDispatcher oosDispatcher_;

  public MessengerService(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue, CommAddress myAddress) throws IOException {
    super(inQueue, outQueue);
    oosDispatcher_ = new CachedOOSDispatcher();
    myAddress_ = myAddress;
  }

  @Override
  public void processMessage(Message msg) {
    if(!(msg instanceof CommMessage)) {
      logger_.error("Don't know what to do with message: " + msg);
      return;
    }
    Socket socket_ = null;
    CommMessage commMsg = (CommMessage) msg;
    if(commMsg.getSourceAddress() == null) {
      logger_.debug("Source address set to null, changing to my address.");
      commMsg.setSourceAddress(myAddress_);
    }
    try {
      ObjectOutputStream oos = oosDispatcher_.get(commMsg.getDestinationAddress());
      oos.writeObject(commMsg);
      logger_.debug("Message: " + commMsg + " sent to: " + commMsg.getDestinationAddress());
    } catch (IOException e) {
      logger_.error("IOException when trying to send: " + msg + ", to: " +
          commMsg.getDestinationAddress() + " " + e);
      outQueue_.add(new ErrorCommMessage((CommMessage) msg, new CommException(
              "Message " + msg + " couldn't be sent.")));
    }
    finally {
      oosDispatcher_.put();
    }
  }
}
