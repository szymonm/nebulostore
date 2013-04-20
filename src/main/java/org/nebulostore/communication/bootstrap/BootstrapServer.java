package org.nebulostore.communication.bootstrap;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import net.tomp2p.p2p.Peer;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.address.CommAddressResolver;
import org.nebulostore.communication.address.PersistentAddressingPeer;
import org.nebulostore.communication.address.TomP2PPeer;
import org.nebulostore.communication.address.TomP2PServer;

/**
 * Simple Bootstrap Server.
 *
 * Chooses its address and puts it into kademlia. Then it simply listens for
 * incoming connections and returns it's address for gossiping.
 *
 * @author Grzegorz Milka
 */
// Using threading multiplexing because each connection is a simple read/write
// and close session without gaps so polling advantage is lost.
//
//  Address garbage collecting will be necessary for completement
//  Possible solutions:
//    Check if kademlia has some timestamping and automatic deletion
//    Clients send "Possibly out of network" message when they can't connect to
//    someone
// TODO(grzegorzmilka) Inactive address cleanup
public class BootstrapServer extends BootstrapService implements Runnable {
  private static Logger logger_ = Logger.getLogger(BootstrapServer.class);
  private ServerSocket serverSocket_;

  private final CommAddress myCommAddress_;
  private final BootstrapMessage myWelcomeMessage_;
  private final InetSocketAddress myInetSocketAddress_;
  private PersistentAddressingPeer pAPeer_;
  private ExecutorService service_ = Executors.newCachedThreadPool();
  private AtomicBoolean isEnding_ = new AtomicBoolean(false);

  /**
   * commAddress - iff not equal to null then myCommAddress is set to it.
   * Otherwise it is random
   */
  @Inject
  public BootstrapServer(
      @Named("BootstrapServerAddress") String bootstrapServerAddress,
      @Named("LocalCommPort") int commCliPort,
      @Named("BootstrapCommPort") int bootstrapPort,
      @Named("LocalAddressingPort") int tomP2PPort,
      @Named("LocalCommAddress") CommAddress commAddress)
    throws NebuloException {
    super(commCliPort, bootstrapPort, tomP2PPort, tomP2PPort);
    myInetSocketAddress_ = new InetSocketAddress(bootstrapServerAddress, commCliPort_);
    bootstrapPort_ = bootstrapPort;
    tomP2PPort_ = tomP2PPort;
    myCommAddress_ = commAddress;
    myWelcomeMessage_ = new BootstrapMessage(myCommAddress_);
    pAPeer_ = null;
    serverSocket_ = null;
    logger_.info("Set server at address: " + myCommAddress_ + ".");

  }

  @Override
  public void startUpService() throws IOException {
    pAPeer_ = new TomP2PServer();
    pAPeer_.setDHTPort(tomP2PPort_);
    pAPeer_.setCommPort(commCliPort_);
    pAPeer_.setBootstrapServerAddress(myInetSocketAddress_.getHostName());
    pAPeer_.setMyCommAddress(myCommAddress_);
    pAPeer_.setUpAndRun();

    serverSocket_ = new ServerSocket(bootstrapPort_);
  }

  @Override
  /**
   * Simple loop accepting handling incoming network connections.
   * @author Grzegorz Milka
   */
  public void run() {
    while (!isEnding_.get()) {
      Socket clientSocket;
      try {
        clientSocket = serverSocket_.accept();
        logger_.info("Accepted connection from: " +
            clientSocket.getRemoteSocketAddress());
      } catch (IOException e) {
        if (isEnding_.get()) {
          logger_.info("IOException on serverSocket during shutdown of " +
              "module: " + e);
        } else {
          logger_.warn("Unexpected IOException on serverSocket: " + e);
        }
        continue;
      }
      service_.execute(new BootstrapProtocol(clientSocket));
    }
  }

  @Override
  public CommAddress getBootstrapCommAddress() {
    return myCommAddress_;
  }

  @Override
  public Peer getTP2PPeer() {
    return ((TomP2PPeer) pAPeer_).getPeer();
  }

  @Override
  public CommAddressResolver getResolver() {
    return pAPeer_.getResolver();
  }

  @Override
  public void shutdownService() {
    isEnding_.set(true);
    pAPeer_.destroy();
    try {
      serverSocket_.close();
    } catch (IOException e) {
      logger_.debug("Error when closing serverSocket: " + e);
    }
    service_.shutdownNow();
  }

  @Override
  public String toString() {
    return "BootstrapServer with CommAddress: " + myCommAddress_ + ", peer: " +
      pAPeer_;
  }

  /**
   * @author Grzegorz Milka
   */
  private class BootstrapProtocol implements Runnable {
    Socket clientSocket_;
    public BootstrapProtocol(Socket clientSocket) {
      clientSocket_ = clientSocket;
    }

    public void run() {
      try {
        BootstrapMessage msg;
        try {
          InputStream socketIS = clientSocket_.getInputStream();
          ObjectInputStream ois = new ObjectInputStream(socketIS);
          msg = (BootstrapMessage) ois.readObject();
        } catch (ClassNotFoundException e) {
          logger_.warn("Error when handling received message " + e);
          return;
        } catch (EOFException e) {
          //GM Possibly client has just checked if this server works
          logger_.debug("EOF at the beginning of connection with: " +
              clientSocket_.getRemoteSocketAddress());
          return;
        }
        logger_.info("Received Hello message from: " + msg.getPeerAddress() +
            "/" + clientSocket_.getRemoteSocketAddress());

        ObjectOutputStream oos = new ObjectOutputStream(
            clientSocket_.getOutputStream());
        oos.writeObject(myWelcomeMessage_);
        logger_.info("Sent Hello message to: " + msg.getPeerAddress() +
            "/" + clientSocket_.getRemoteSocketAddress());
      } catch (IOException e) {
        logger_.warn("IOException when handling client: " +
            clientSocket_.getRemoteSocketAddress() + ", error: " + e);
      } finally {
        try {
          logger_.info("Closing connection with client: " +
              clientSocket_.getRemoteSocketAddress());
          clientSocket_.close();
        } catch (IOException e) {
          logger_.debug("IOException when closing client's socket: " + e);
        }
      }
    }
  }
}
