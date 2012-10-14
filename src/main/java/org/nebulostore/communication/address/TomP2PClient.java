package org.nebulostore.communication.address;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

/**
 * TomP2P's kademlia based implementation of persistent addressing service.
 *
 * @author Grzegorz Milka
 */
//NOTE-GM Perhaps add TomP2PBuilder (Builder pattern)
public final class TomP2PClient extends TomP2PPeer {
  // 4 seconds
  private static final int ADDRESS_DISCOVERY_PERIOD = 4000;

  private Timer currentAddressDiscoverer_;

  public TomP2PClient() {
    super();
  }

  @Override
  public void setUpAndRun() throws IOException {
    if (bootstrapServerAddress_ == null) {
      throw new IllegalStateException("Bootstrap address has to be set.");
    }
    if (myCommAddress_ == null) {
      throw new IllegalStateException("CommAddress has to be set.");
    }

    try {
      myPeer_ = new PeerMaker(new Number160(myCommAddress_.hashCode())).
        setPorts(tomp2pPort_).makeAndListen();
    } catch (IOException e) {
      String errMsg = "Error when making peer";
      logger_.error(errMsg + " " + e);
      throw e;
    }

    bootstrapServerPeerAddress_ = new PeerAddress(Number160.ZERO,
        new InetSocketAddress(bootstrapServerAddress_, tomp2pPort_));

    myPeer_.getConfiguration().setBehindFirewall(true);

    FutureDiscover discovery = myPeer_.discover().
      setPeerAddress(bootstrapServerPeerAddress_).start();
    discovery.awaitUninterruptibly();
    if (!discovery.isSuccess()) {
      String errMsg = "Couldn't perform tomp2p discovery: " +
        discovery.getFailedReason();
      logger_.error(errMsg);
      myPeer_ = null;
      throw new IOException(errMsg);
    }
    logger_.debug("Peer: " + discovery.getReporter() +
        " told us about our address.");
    myInetSocketAddress_ = new InetSocketAddress(
        myPeer_.getPeerAddress().getInetAddress(), commCliPort_);

    bootstrapServerPeerAddress_ = discovery.getReporter();
    FutureBootstrap bootstrap = myPeer_.bootstrap().
      setPeerAddress(bootstrapServerPeerAddress_).start();
    bootstrap.awaitUninterruptibly();
    if (!bootstrap.isSuccess()) {
      String errMsg = "Couldn't perform tomp2p bootstrap: " +
        bootstrap.getFailedReason();
      logger_.error(errMsg);
      myPeer_ = null;
      throw new IOException(errMsg);
    }

    try {
      uploadCurrentInetSocketAddress();
    } catch (IOException e) {
      logger_.warn(e.getMessage() + e.getCause().toString());
    }

    // Set up deamon to discover my address periodically
    currentAddressDiscoverer_ = new Timer(true);
    currentAddressDiscoverer_.schedule(new CurrentAddressDiscoverer(),
        ADDRESS_DISCOVERY_PERIOD, ADDRESS_DISCOVERY_PERIOD);

    resolver_ = new HashAddressResolver(myCommAddress_, myPeer_);

    logger_.info("TomP2P initialization finished. My address is: " +
        myInetSocketAddress_ + ".");
  }

  @Override
  public void destroy() {
    checkSetUp();
    synchronized (isTearingDown_) {
      isTearingDown_ = true;
    }
    logger_.info("Starting tearDown procedure.");
    currentAddressDiscoverer_.cancel();
    logger_.info("CurrentAddressDiscoverer canceled.");

    myPeer_.shutdown();
    resolver_ = null;
    myPeer_ = null;
    logger_.info("TomP2P peer has been shut down.");
  }

  @Override
  public ICommAddressResolver getResolver() {
    checkSetUp();
    return resolver_;
  }

  @Override
  public InetSocketAddress getCurrentInetSocketAddress() throws IOException {
    checkSetUp();
    FutureDiscover discovery = myPeer_.discover().
      setPeerAddress(bootstrapServerPeerAddress_).start();

    discovery.awaitUninterruptibly();
    if (!discovery.isSuccess()) {
      String errMsg = "Couldn't perform tomp2p discovery: " +
        discovery.getFailedReason();
      logger_.warn(errMsg);
      throw new IOException(errMsg);
    }
    logger_.trace("Peer: " + discovery.getReporter() +
        " told us about our address.");
    InetSocketAddress myInetSocketAddress = new InetSocketAddress(
        myPeer_.getPeerAddress().getInetAddress(), commCliPort_);
    return myInetSocketAddress;
  }

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
      synchronized (isTearingDown_) {
        if (isTearingDown_) {
          return;
        }
        logger_.trace("Running periodical address discovery.");
        InetSocketAddress myInetSocketAddress;
        try {
          myInetSocketAddress = getCurrentInetSocketAddress();
        } catch (IOException e) {
          logger_.warn("Couldn't discover current external address.");
          return;
        }

        if (!myInetSocketAddress_.equals(myInetSocketAddress)) {
          logger_.info("Discovered change in network address from: " +
              myInetSocketAddress_ + " to: " + myInetSocketAddress + ".");
          try {
            uploadCurrentInetSocketAddress(myInetSocketAddress);
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
  }
}
