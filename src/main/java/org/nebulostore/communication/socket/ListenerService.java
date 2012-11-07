package org.nebulostore.communication.socket;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;

/**
 * Module responsible for receiving CommMessages through TCP.
 *
 * Module simply listens for incomming TCP connections and passes serialized
 * CommMessages back to CommunicationPeer through outQueue_.
 *
 * @author Grzegorz Milka
 */
public class ListenerService extends Module {
  private ServerSocket serverSocket_;
  private int commCliPort_;
  private static Logger logger_ = Logger.getLogger(ListenerService.class);
  private ExecutorService service_ = Executors.newCachedThreadPool();
  private Boolean isEnding_ = false;

  /**
   * Handler for incoming connection.
   *
   * @author Grzegorz Milka
   */
  private class ListenerProtocol implements Runnable {
    Socket clientSocket_;
    public ListenerProtocol(Socket clientSocket) {
      if (clientSocket == null)
        throw new NullPointerException();
      clientSocket_ = clientSocket;
    }

    //TODO(grzegorzmilka) Add Interrupt handler.
    public void run() {
      Message msg = null;
      try {
        InputStream sis = clientSocket_.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(sis);
        while (true) {
          msg = (Message) ois.readObject();
          outQueue_.add(msg);
          logger_.debug("Added received message: " + msg + " to outgoing queue");
        }
      } catch (ClassNotFoundException e) {
        logger_.error("Error when handling received message " + e);
      } catch (EOFException e) {
        logger_.debug("End of connection with: " +
            clientSocket_.getRemoteSocketAddress() + " " + e);
      } catch (IOException e) {
        logger_.error("IOException in connection with: " +
            clientSocket_.getRemoteSocketAddress() + " " + e);
      } finally {
        try {
          clientSocket_.close();
        } catch (IOException e) {
          logger_.trace("Exception when closing client's socket: " + e);
        }
      }
    }
  }

  public ListenerService(BlockingQueue<Message> outQueue, int commCliPort)
    throws IOException {
    super(null, outQueue);
    commCliPort_ = commCliPort;
    try {
      serverSocket_ = new ServerSocket(commCliPort_);
    } catch (IOException e) {
      logger_.error("Could not initialize listening socket on port: " +
              commCliPort_ + ", due to IOException: " + e);
      throw new IOException("Could not initialize listening socket " +
          "due to IOException.", e);
    }
    logger_.info("Created listenerService's socket on port: " + commCliPort_);
    try {
      serverSocket_.setReuseAddress(true);
    } catch (SocketException e) {
      logger_.warn("Couldn't set serverSocket to reuse address: " + e);
    }
  }

  @Override
  public void run() {
    boolean isEnding;
    synchronized (isEnding_) {
      isEnding = isEnding_;
    }
    while (!isEnding) {
      Socket clientSocket = null;
      try {
        clientSocket = serverSocket_.accept();
      } catch (IOException e) {
        logger_.error("IOException when accepting connection " + e);
        continue;
      } finally {
        synchronized (isEnding_) {
          isEnding = isEnding_;
        }
      }
      logger_.debug("Accepted connection from: " +
          clientSocket.getRemoteSocketAddress());
      service_.execute(new ListenerProtocol(clientSocket));
    }
  }

  @Override
  public void endModule() {
    synchronized (isEnding_) {
      isEnding_ = true;
    }
    try {
      serverSocket_.close();
    } catch (IOException e) {
      logger_.error("Error when closing serverSocket: " + e);
    }
    service_.shutdownNow();
    super.endModule();
  }

  @Override
  public void processMessage(Message msg) {
    throw new UnsupportedOperationException(
        "Incoming message to ListenerService should never happen.");
  }
}
