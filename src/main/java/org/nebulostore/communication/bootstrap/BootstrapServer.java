package org.nebulostore.communication.bootstrap;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;

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
// TODO-GM Inactive address cleanup
public class BootstrapServer extends BootstrapService implements Runnable {
  private static Logger logger_ = Logger.getLogger(BootstrapServer.class);
  private ServerSocket serverSocket_;

  private final CommAddress myCommAddress_;
  private final BootstrapMessage myWelcomeMessage_;
  // TODO-GM make some ip address discovery here like in client
  private final InetSocketAddress myInetSocketAddress_ =
    new InetSocketAddress("planetlab1.ci.pwr.wroc.pl", commCliPort_);
  private final Peer myPeer_;
  private final CommAddressResolver resolver_;

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
          logger_.error("Error when handling received message " + e);
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
        logger_.error("IOException when handling client: " +
            clientSocket_.getRemoteSocketAddress() + ", error: " + e);
      } finally {
        try {
          logger_.info("Closing connection with client: " +
              clientSocket_.getRemoteSocketAddress());
          clientSocket_.close();
        } catch (IOException e) {
          logger_.error("IOException when closing client's socket: " + e);
        }
      }
    }
  }

  public BootstrapServer(int commCliPort) throws IOException, NebuloException {
    this(commCliPort, BOOTSTRAP_PORT, TOMP2P_PORT);
  }

  public BootstrapServer(int commCliPort, int bootstrapPort, int tomp2pPort)
    throws IOException, NebuloException {
    super(commCliPort);
    bootstrapPort_ = bootstrapPort;
    tomp2pPort_ = tomp2pPort;
    myCommAddress_ = CommAddress.newRandomCommAddress();
    myWelcomeMessage_ = new BootstrapMessage(myCommAddress_);
    logger_.info("Set server at address: " + myCommAddress_ + ".");
    myPeer_ = new PeerMaker(new Number160(myCommAddress_.hashCode())).
      setPorts(tomp2pPort_).makeAndListen();

    // UpdateMyAddress to DHT
    try {
      myPeer_.put(new Number160(myCommAddress_.hashCode())).
        setData(new Data(myInetSocketAddress_)).start().
        awaitUninterruptibly();
    } catch (IOException e) {
      String errMsg = "Error when trying to update address to kademlia";
      logger_.error(errMsg + " " + e);
      throw new NebuloException(errMsg, e);
    }
    logger_.info("Info about my address has been put to kademlia.");

    resolver_ = new HashAddressResolver(myCommAddress_, myPeer_);

    serverSocket_ = new ServerSocket(bootstrapPort_);
  }

  @Override
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
  public CommAddress getBootstrapCommAddress() {
    return myCommAddress_;
  }

  @Override
  public CommAddressResolver getResolver() {
    return resolver_;
  }

  @Override
  public String toString() {
    return "BootstrapServer with CommAddress: " + myCommAddress_ + ", peer: " +
      myPeer_;
  }
}
