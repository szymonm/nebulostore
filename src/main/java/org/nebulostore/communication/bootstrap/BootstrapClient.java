package org.nebulostore.communication.bootstrap;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.io.EOFException;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.bootstrap.BootstrapMessage;
import org.nebulostore.communication.bootstrap.CommAddressResolver;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.DiscoveryMessage;

import static org.nebulostore.communication.bootstrap.BootstrapMessageType.*;

import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

/**
 * Bootstrap Client. 
 * BootstrapClient makes initial contact with BootstrapServer signaling
 * its entry to the nebulostore network and getting list of hosts present in the
 * network. 
 * It also handles persistent addressing.
 *
 * @author Grzegorz Milka
 */
public class BootstrapClient extends Module {
  private static Logger logger_ = Logger.getLogger(BootstrapClient.class);

  private final int ADDRESS_DISCOVERY_PERIOD = 4000; // 4 seconds

  private final String bootstrapServerAddress_ = "planetlab1.ci.pwr.wroc.pl";
  private static final int COMM_CLI_PORT_ = 9987;
  private static final int BOOTSTRAP_CLI_PORT_ = 9989;
  private static final int BOOTSTRAP_SERV_PORT_ = 9991;
  private static final int TOMP2P_PORT_ = 9993;
  private int commCliPort_ = COMM_CLI_PORT_; 
  private int bootstrapCliPort_ = BOOTSTRAP_CLI_PORT_;
  private int bootstrapServPort_ = BOOTSTRAP_SERV_PORT_;
  private int tomp2pPort_ = TOMP2P_PORT_; 

  private static BootstrapMessage keepAliveMsg_;
  private static BootstrapMessage peerDiscoveryMsg_;

  //NOTE-GM Addresses for normal communication
  private CommAddress myCommAddress_;
  private InetSocketAddress myInetSocketAddress_;
  //NOTE-GM TomP2P communication
  private Peer myPeer_;
  private PeerAddress bootstrapServerPeerAddress_;
  private final CommAddressResolver resolver_;

  /**
   * Discovers current external address.
   * Runs every ADDRESS_DISCOVERY_PERIOD miliseconds to find if our internet
   * address has changed. If so it tries to change it.
   *
   * If a try to change has failed it returns quietly, perhaps internet is down
   * and it needs to wait.
   * @author Grzegorz Milka
   */
  private class CurrentAddressDiscoverer extends TimerTask {
    public void run() {
      logger_.trace("Running periodical address discovery.");
      FutureDiscover discovery = myPeer_.discover().
        setPeerAddress(bootstrapServerPeerAddress_).start();
      discovery.awaitUninterruptibly();
      if( !discovery.isSuccess() ) {
        String errMsg = "Couldn't perform tomp2p discovery: " + 
          discovery.getFailedReason();
        logger_.error(errMsg);
        return;
      }
      logger_.trace("Peer: " + discovery.getReporter() + " told us about our address.");
      InetSocketAddress myInetSocketAddress = new InetSocketAddress(
          myPeer_.getPeerAddress().getInetAddress(), commCliPort_);

      if(! myInetSocketAddress_.equals(myInetSocketAddress) ) {
        logger_.info("Discovered change in network address from: " + 
            myInetSocketAddress_ + " to: " + myInetSocketAddress);
        try {
          myPeer_.put(new Number160(myCommAddress_.hashCode())).
            setData(new Data(myInetSocketAddress_)).start().
            awaitUninterruptibly();
        } catch (IOException e) {
          String errMsg = "Error when trying to update address to kademlia";
          logger_.error(errMsg + " " + e);
          return;
        }
        logger_.info("Info about my address has been put to kademlia."); 
        myInetSocketAddress_ = myInetSocketAddress;
      }
    }
  }

  private class PeerFoundListener implements Runnable {
    private ServerSocket serverSocket;
    public PeerFoundListener() throws IOException {
      serverSocket = new ServerSocket(bootstrapCliPort_);
    }
    public void run() {
      while(true) {
        try {
          Socket socket = serverSocket.accept();
          ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
          BootstrapMessage msg = (BootstrapMessage) ois.readObject();
          if(msg.getType() != PEER_INFO) {
            logger_.error("Received message different than PEER_INFO");
          }
          else {
            outQueue_.add(new CommPeerFoundMessage(msg.getPeerAddress(),
                  myCommAddress_));
          }
          socket.close();
        } catch (IOException e) {
          logger_.error("Error when receiving peer info" + e);
          //TODO We might ask for resending peer info
        } catch (ClassNotFoundException e) {
          logger_.error("Error when receiving peer info" + e);
        }

      }
    }
  }

  public BootstrapClient(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue, int commCliPort) throws NebuloException {
    super(inQueue, outQueue);
    commCliPort_ = commCliPort;

    // Find my address
    logger_.info("Finding out my address.");
    myCommAddress_ = new CommAddress();
    bootstrapServerPeerAddress_ = new PeerAddress(Number160.ZERO, 
        new InetSocketAddress(bootstrapServerAddress_, tomp2pPort_));

    //Discover and bootstrap to TomP2P's kademlia network.

    //NOTE-GM: hashCode returns only 32-bit int it might be better to change it
    try {
      myPeer_ = new PeerMaker(new Number160(myCommAddress_.hashCode())).
        setPorts(tomp2pPort_).makeAndListen();
    } catch (IOException e) {
      String errMsg = "Error when making peer";
      logger_.error(errMsg + " " + e);
      throw new NebuloException(errMsg, e);
    }

    FutureDiscover discovery = myPeer_.discover().
      setPeerAddress(bootstrapServerPeerAddress_).start();
    discovery.awaitUninterruptibly();
    if( !discovery.isSuccess() ) {
      String errMsg = "Couldn't perform tomp2p discovery: " + 
        discovery.getFailedReason();
      logger_.error(errMsg);
      throw new NebuloException(errMsg);
    }
    logger_.debug("Peer: " + discovery.getReporter() + " told us about our address.");
    bootstrapServerPeerAddress_ = discovery.getReporter();
    FutureBootstrap bootstrap = myPeer_.bootstrap().
      setPeerAddress(bootstrapServerPeerAddress_).start();
    bootstrap.awaitUninterruptibly();
    if( !bootstrap.isSuccess() ) {
      String errMsg = "Couldn't perform tomp2p bootstrap: " + 
        bootstrap.getFailedReason();
      logger_.error(errMsg);
      throw new NebuloException(errMsg);
    }
    myInetSocketAddress_ = new InetSocketAddress(
        myPeer_.getPeerAddress().getInetAddress(), commCliPort_);

    logger_.info("TomP2P initialization finished. My address is: " + 
        myInetSocketAddress_ + ".");

    //TomP2P initialization finished.

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
    Timer currentAddressDiscoverer = new Timer();
    currentAddressDiscoverer.schedule(new CurrentAddressDiscoverer(), 
        ADDRESS_DISCOVERY_PERIOD, ADDRESS_DISCOVERY_PERIOD);
    logger_.info("Started CurrentAddressDiscoverer.");
    //TODO-GM DELETE IT
    try {
      logger_.debug("Resolver resolved my address to: " + 
          resolver_.resolve(myCommAddress_) + "."); 
    } catch (IOException e) {
      throw new NebuloException(e);
    }

    //TODO-GM Collision handling
    //TODO-GM Automic refreshing handling

    keepAliveMsg_ = new BootstrapMessage(KEEP_ALIVE, myCommAddress_);
    peerDiscoveryMsg_ = new BootstrapMessage(PEER_DISCOVERY, myCommAddress_);

    // Start listening for new peers
    Executor exec = Executors.newSingleThreadExecutor();
    try {
      exec.execute(new PeerFoundListener());
    } catch (IOException e) {
      logger_.error("Error when trying to initilize bootstrap listener " + e);
      throw new NebuloException("Error when trying to initilize bootstrap listener", e);
    }

    // Send hello keep alive
    while(true) {
      try {
        sendKeepAliveMsg();
        break;
      } catch (IOException e) {
        logger_.error("Error when sending hello message " + e);
      }
    }

    // Get all current peers
    while(true) {
      try {
        sendAndListenPeerDiscoveryMsg();
        break;
      } catch (IOException e) {
        logger_.error("Error when sending peer discovery message " + e);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException err) {
          // ignore
        }
      }
    }
  }

  @Override
  protected void processMessage(Message msg) {
    if (msg instanceof DiscoveryMessage) {
      while(true) {
        try {
          sendAndListenPeerDiscoveryMsg();
          break;
        } catch (IOException e) {
          logger_.error("Error when sending peer discovery message " + e);
        }
      }
    }
  }

  private void sendKeepAliveMsg() throws IOException {
    logger_.info("Sending KEEP_ALIVE to server.");
    Socket socket = new Socket(bootstrapServerAddress_, bootstrapServPort_);
    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
    oos.writeObject(keepAliveMsg_);
    socket.close();
  }

  private void sendAndListenPeerDiscoveryMsg() throws IOException {
    logger_.info("Sending PEER_DISCOVERY to server.");
    Socket socket = new Socket(bootstrapServerAddress_, bootstrapServPort_);
    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
    oos.writeObject(peerDiscoveryMsg_);
    logger_.info("PEER_DISCOVERY sent to server.");
    BootstrapMessage msg = null;
    try {
      ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
      while(! socket.isInputShutdown()) {
        msg = (BootstrapMessage) ois.readObject();
        if(msg.getType() != PEER_INFO) {
          logger_.error("Received message different than PEER_INFO");
        }
        else {
          outQueue_.add(new CommPeerFoundMessage(msg.getPeerAddress(),
                myCommAddress_));
        }
      }
    } catch (EOFException e) {
      //ignore
    } catch (ClassNotFoundException e) {
      String errMsg = "Received message different than BootstrapMessage: " + 
        msg + "as a response to PEER_DISCOVERY.";
      logger_.error(errMsg);
      throw new IOException(errMsg, e);
    }
    finally {
      socket.close();
    }
  }

  public CommAddressResolver getResolver() {
    return resolver_;
  }

  @Override
  public String toString() {
    return "BootstrapClient with address: " + myCommAddress_ + ", peer: " +
      myPeer_ + ", socketAddress: " + myInetSocketAddress_;
  }
}
