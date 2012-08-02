package org.nebulostore.communication.bootstrap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.exceptions.AddressNotPresentException;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytes;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

/**
 * Bootstrap Client.
 * BootstrapClient makes initial contact with BootstrapServer signaling
 * its entry to the nebulostore network and getting its address for gossiping
 * It also handles persistent addressing.
 *
 * @author Grzegorz Milka
 */
public final class BootstrapClient extends BootstrapService {
  private static Logger logger_ = Logger.getLogger(BootstrapClient.class);

  // 4 seconds
  private static final int ADDRESS_DISCOVERY_PERIOD = 4000;

  private final String bootstrapServerAddress_ = "planetlab1.ci.pwr.wroc.pl";

  //NOTE-GM Addresses for normal communication
  private CommAddress myCommAddress_;
  private InetSocketAddress myInetSocketAddress_;
  //NOTE-GM TomP2P communication
  private Peer myPeer_;
  private PeerAddress bootstrapServerPeerAddress_;
  private CommAddress bootstrapServerCommAddress_;
  private final CommAddressResolver resolver_;

  /**
   * Discovers current external address.
   * Runs every ADDRESS_DISCOVERY_PERIOD miliseconds to find if our internet
   * address has changed. If so it tries to change it.
   *
   * If a try to change has failed it returns quietly, perhaps internet is down
   * and it needs to wait.
   *
   * @author Grzegorz Milka
   */
  private class CurrentAddressDiscoverer extends TimerTask {
    @Override
    public void run() {
      logger_.trace("Running periodical address discovery.");
      FutureDiscover discovery = myPeer_.discover().
        setPeerAddress(bootstrapServerPeerAddress_).start();
      discovery.awaitUninterruptibly();
      if (!discovery.isSuccess()) {
        String errMsg = "Couldn't perform tomp2p discovery: " +
          discovery.getFailedReason();
        logger_.error(errMsg);
        return;
      }
      logger_.trace("Peer: " + discovery.getReporter() + " told us about our address.");
      InetSocketAddress myInetSocketAddress = new InetSocketAddress(
          myPeer_.getPeerAddress().getInetAddress(), commCliPort_);

      if (!myInetSocketAddress_.equals(myInetSocketAddress)) {
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

  //TODO-GM Collision handling
  public BootstrapClient(int commCliPort) throws NebuloException {
    super(commCliPort);

    // Find my address
    logger_.info("Finding out my address.");
    myCommAddress_ = CommAddress.newRandomCommAddress();
    bootstrapServerPeerAddress_ = new PeerAddress(Number160.ZERO,
        new InetSocketAddress(bootstrapServerAddress_, tomp2pPort_));

    //Discover and bootstrap to TomP2P's kademlia network.

    //NOTE-GM: hashCode returns only 32-bit int it might be better to change it
    try {
      myPeer_ = new PeerMaker(new Number160(myCommAddress_.hashCode())).
        setPorts(tomp2pPort_).makeAndListen();
      myPeer_.getConfiguration().setBehindFirewall(true);
    } catch (IOException e) {
      String errMsg = "Error when making peer";
      logger_.error(errMsg + " " + e);
      throw new NebuloException(errMsg, e);
    }

    FutureDiscover discovery = myPeer_.discover().
      setPeerAddress(bootstrapServerPeerAddress_).start();
    discovery.awaitUninterruptibly();
    if (!discovery.isSuccess()) {
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
    if (!bootstrap.isSuccess()) {
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
    //
    //Set up Upnp port mapping

    try {
      setUpUpnpPortMapping();
      logger_.info("Upnp port mapping set up.");
    } catch (IOException e) {
      logger_.error("Couldn't set up Upnp port mapping: " + e);
    }

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
    //TODO-GM DELETE IT. It was added just as safety measure
    try {
      logger_.debug("Resolver resolved my address to: " +
          resolver_.resolve(myCommAddress_) + ".");
    } catch (IOException e) {
      throw new NebuloException(e);
    } catch (AddressNotPresentException e) {
      //Something is really bad if this has happened
      throw new NebuloException(e);
    }


    // Send hello keep alive
    while (true) {
      try {
        sendAndReceiveHelloMsg();
        break;
      } catch (IOException e) {
        logger_.error("Error when sending hello message " + e);
      }
    }
  }

  @Override
  public CommAddress getBootstrapCommAddress() {
    return bootstrapServerCommAddress_;
  }

  @Override
  public CommAddressResolver getResolver() {
    return resolver_;
  }

  @Override
  public String toString() {
    return "BootstrapClient with address: " +
      myCommAddress_ + ", peer: " + myPeer_ +
      ", socketAddress: " + myInetSocketAddress_;
  }

  private void setUpUpnpPortMapping() throws IOException {
    InetAddress myLocalAddr = InetAddress.getLocalHost();
    PortMapping desiredMapping = new PortMapping(commCliPort_,
        myLocalAddr.toString(),
        PortMapping.Protocol.TCP);
    desiredMapping.setExternalPort(new UnsignedIntegerTwoBytes(commCliPort_));
    desiredMapping.setInternalPort(new UnsignedIntegerTwoBytes(commCliPort_));

    UpnpService upnpService =
      new UpnpServiceImpl(new PortMappingListener(desiredMapping));

    upnpService.getControlPoint().search();
  }

  private void sendAndReceiveHelloMsg() throws IOException {
    logger_.info("Sending Hello message to server.");
    Socket socket = new Socket(bootstrapServerAddress_, bootstrapPort_);
    try {
      ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
      oos.writeObject(new BootstrapMessage(myCommAddress_));
      logger_.info("Sent Hello message to server.");
      ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
      BootstrapMessage message = (BootstrapMessage) ois.readObject();
      bootstrapServerCommAddress_ = message.getPeerAddress();
      logger_.info("Received Hello message from server. His address: " +
          bootstrapServerCommAddress_);
    } catch (IOException e) {
      throw e;
    } catch (ClassNotFoundException e) {
      String errMsg = "Read object is not BootstrapMessage.";
      logger_.error("errMsg");
      throw new IOException(errMsg, e);
    } finally {
      socket.close();
    }
  }
}
