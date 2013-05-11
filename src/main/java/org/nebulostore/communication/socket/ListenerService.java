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
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.modules.Module;

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
  private AtomicBoolean isEnding_ = new AtomicBoolean(false);

  /**
   * Handler for incoming connection.
   *
   * @author Grzegorz Milka
   */
  private class ListenerProtocol implements Runnable {
    Socket clientSocket_;
    public ListenerProtocol(Socket clientSocket) {
      if (clientSocket == null) {
        throw new IllegalArgumentException("Client Socket can not be null");
      }
      clientSocket_ = clientSocket;
    }

    public void run() {
      logger_.trace("Running ListenerProtocol accepting connection from: " +
          clientSocket_.getRemoteSocketAddress());
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
        logger_.warn("Error when handling received message: " + e);
      } catch (EOFException e) {
        logger_.debug("End of connection with: " +
            clientSocket_.getRemoteSocketAddress() + " " + e);
      } catch (IOException e) {
        logger_.warn("IOException in connection with: " +
            clientSocket_.getRemoteSocketAddress() + " " + e);
      } finally {
        logger_.trace("Closing ListenerProtocol connection with host: " +
            clientSocket_.getRemoteSocketAddress());
        try {
          clientSocket_.close();
        } catch (IOException e) {
          logger_.trace("Exception when closing client's socket: " + e);
        }
      }
    }
  }

  @Inject
  public ListenerService(
      @Named("CommunicationPeerInQueue") BlockingQueue<Message> outQueue,
      @Named("communication.ports.comm-cli-port") int commCliPort)
    /* throws IOException */ {
    super(null, outQueue);
    commCliPort_ = commCliPort;
  }

  @Override
  public void run() {
    try {
      serverSocket_ = new ServerSocket(commCliPort_);
    } catch (IOException e) {
      logger_.error("Could not initialize listening socket on port: " +
              commCliPort_ + ", due to IOException: " + e);
      /* Throwing runtime exception since run can not throw checked exception */
      throw new RuntimeException("Could not initialize listening socket " +
          "due to IOException.", e);
    }
    logger_.info("Created listenerService's socket on port: " + commCliPort_);

    try {
      serverSocket_.setReuseAddress(true);
    } catch (SocketException e) {
      logger_.warn("Couldn't set serverSocket to reuse address: " + e);
    }

    while (!isEnding_.get()) {
      Socket clientSocket = null;
      try {
        clientSocket = serverSocket_.accept();
      } catch (IOException e) {
        logger_.warn("IOException when accepting connection " + e);
        continue;
      }
      logger_.debug("Accepted connection from: " +
          clientSocket.getRemoteSocketAddress());
      service_.execute(new ListenerProtocol(clientSocket));
    }
  }

  public void shutdown() {
    isEnding_.set(true);
    try {
      serverSocket_.close();
    } catch (IOException e) {
      logger_.trace("Error when closing serverSocket: " + e);
    }
    service_.shutdownNow();
    endModule();
  }

  @Override
  public void processMessage(Message msg) {
    throw new UnsupportedOperationException(
        "Incoming message to ListenerService should never happen.");
  }
}
