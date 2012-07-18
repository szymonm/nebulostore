package org.nebulostore.communication.bootstrap;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.LinkedHashSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import org.nebulostore.communication.bootstrap.BootstrapMessage;
import org.nebulostore.communication.bootstrap.CommAddressResolver;
import org.nebulostore.communication.bootstrap.HashAddressResolver;
import org.nebulostore.communication.address.CommAddress;
import static org.nebulostore.communication.bootstrap.BootstrapMessageType.*;

/**
 * Simple UDP Bootstrap Server. 
 * BootstrapServer maintains a collection of all hosts in the nebulostore
 * network and serves this list to all joining hosts. 
 *
 * @author Grzegorz Milka
 */
// Using threading multiplexing because each connection is a simple read/write
// and close session without gaps so polling advantage is lost.
public class BootstrapServer implements Runnable {
  private static Logger logger_ = Logger.getLogger(BootstrapServer.class);
  private static final int BOOTSTRAP_CLI_PORT_ = 9989;
  private static final int BOOTSTRAP_SERV_PORT_ = 9991;
  private static final int TOMP2P_PORT_ = 9993;
  private int bootstrapCliPort_ = BOOTSTRAP_CLI_PORT_;
  private int bootstrapServPort_ = BOOTSTRAP_SERV_PORT_;
  private int tomp2pPort_ = TOMP2P_PORT_; 
  private ServerSocket serverSocket_;
  private Set<CommAddress> presentHosts = Collections.synchronizedSet(
      new LinkedHashSet<CommAddress>());

  private final UUID myUUID_;
  private final Peer myPeer_;
  private final CommAddressResolver resolver_;

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
          msg = (BootstrapMessage)ois.readObject();
        } catch (ClassNotFoundException e) {
          logger_.error("Error when handling received message " + e);
          return;
        } catch (EOFException e) {
          //GM Possibly client has just checked if this server works
          logger_.debug("EOF at the beginning of connection with: " +
              clientSocket_.getRemoteSocketAddress());
          return;
        }

        switch(msg.getType()) {
          case PEER_INFO:
            logger_.error("PEER_INFO sent to server.");
            break;
          case KEEP_ALIVE:
            logger_.info("Received KEEP_ALIVE message from " +
                clientSocket_.getRemoteSocketAddress());
            CommAddress clientAddress_ = msg.getPeerAddress();
            boolean isAbsent_ = false;
            synchronized(presentHosts) {
              isAbsent_ = !presentHosts.contains(clientAddress_);
            }
            if(isAbsent_) {
              logger_.debug("Host: " + clientAddress_ + " not present, broadcasting...");
              BootstrapMessage peerInfoMsg = 
                new BootstrapMessage(BootstrapMessageType.PEER_INFO, clientAddress_);
              Set<CommAddress> peers = new LinkedHashSet<CommAddress>();
              synchronized (presentHosts) {
                peers.addAll(presentHosts);
                presentHosts.add(clientAddress_);
              }
              logger_.debug("Sending PEER_INFO to " + peers.size() + " peers.");
              for(CommAddress host: peers) {
                Socket hostSocket = null;
                try {
                  logger_.debug("Sending PEER_INFO about: " + clientAddress_ + 
                      " to: " + resolver_.resolve(host).getAddress());
                  hostSocket = new Socket(resolver_.resolve(host).getAddress(),
                      bootstrapCliPort_);
                  ObjectOutputStream oos = 
                    new ObjectOutputStream(hostSocket.getOutputStream());
                  oos.writeObject(peerInfoMsg);
                  hostSocket.close();
                } catch(IOException e) {
                  logger_.error("IOException when sending peer info to: " + 
                      host + ", error: "  + e);
                }
                finally{
                  try {
                    if(hostSocket != null)
                      hostSocket.close();
                  } catch(IOException e) {
                    logger_.error("IOException when closing client's socket: " + e);
                  }
                }
              }
            }
            break;

          case PEER_DISCOVERY:
            logger_.info("Received PEER_DISCOVERY message from " + 
                clientSocket_.getRemoteSocketAddress());
            Set<CommAddress> peers = new LinkedHashSet<CommAddress>();
            synchronized (presentHosts) {
              peers.addAll(presentHosts);
            }
            ObjectOutputStream oos = new ObjectOutputStream(
                clientSocket_.getOutputStream());
            for(CommAddress peer: peers) {
              BootstrapMessage peerInfoMsg = new BootstrapMessage(PEER_INFO, peer);
              logger_.debug("Sending PEER_INFO about: " + peer + 
                  " to: " + clientSocket_.getRemoteSocketAddress());
              oos.writeObject(peerInfoMsg);
            }
            break;
        }
      } catch (IOException e) {
        logger_.error("IOException when handling client: " +
            clientSocket_.getRemoteSocketAddress() + ", error: " + e);
      }
      finally {
        try {
          logger_.info("Closing connection with client: " +
              clientSocket_.getRemoteSocketAddress());
          clientSocket_.close();
        } catch(IOException e) {
          logger_.error("IOException when closing client's socket: " + e);
        }
      }
    }
  }

  public BootstrapServer() throws IOException {
    this(BOOTSTRAP_CLI_PORT_, BOOTSTRAP_SERV_PORT_, TOMP2P_PORT_);
  }

  public BootstrapServer(int bootstrapCliPort, int bootstrapServPort, 
      int tomp2pPort) throws IOException{
    bootstrapCliPort_ = bootstrapCliPort;
    bootstrapServPort_ = bootstrapServPort;
    tomp2pPort_ = tomp2pPort;
    myUUID_ = UUID.randomUUID();
    myPeer_ = new PeerMaker(new Number160(myUUID_.hashCode())).
      setPorts(tomp2pPort_).makeAndListen();
    resolver_ = new HashAddressResolver(CommAddress.getZero(), myPeer_);

    serverSocket_ = new ServerSocket(bootstrapServPort_);
  }

  public static void main(String[] args) throws IOException{
    DOMConfigurator.configure("resources/conf/log4j.xml");
    try {
      Executor exec = Executors.newSingleThreadExecutor();
      logger_.info("Starting BootstrapServer");
      exec.execute(new BootstrapServer());
    } catch(IOException e) {
      logger_.fatal("IOException when executing BootstrapServer: " + e);
      throw e;
    }
  }

  public void run() {
    Executor service = Executors.newCachedThreadPool();

    while (true) {
      Socket clientSocket;
      try {
        clientSocket = serverSocket_.accept();
        logger_.info("Accepted connection from: " + 
            clientSocket.getRemoteSocketAddress());
      } catch (IOException e) {
        logger_.error("IOException when accepting connection " + e);
        continue;
      }
      service.execute(new BootstrapProtocol(clientSocket));
    }
  }

  @Override
  public String toString() {
    return "BootstrapServer with UUID: " + myUUID_ + ", peer: " + myPeer_;

  }

}
