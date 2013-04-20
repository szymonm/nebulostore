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
  private static final int MAX_RETRIES = 10;

  private Timer currentAddressDiscoverer_;

  public TomP2PClient() {
    super();
  }

  @Override
  public void setUpAndRun() throws IOException {
    // TODO(grzegorzmilka): Fix this workaround.
    boolean success = false;
    for (int i = 1; i <= MAX_RETRIES; ++i) {
      if (bootstrapServerAddress_ == null) {
        throw new IllegalStateException("Bootstrap address has to be set.");
      }
      if (myCommAddress_ == null) {
        throw new IllegalStateException("CommAddress has to be set.");
      }

      try {
        /* Shutdown peer if it exists, before running new peer */
        if (myPeer_ != null) {
          myPeer_.shutdown();
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            /* Ignore */
            logger_.debug("Received interrupt while waiting for peer " +
                "to shutdown.");
          }
        }
        myPeer_ = new PeerMaker(new Number160(myCommAddress_.hashCode())).
            setPorts(tomP2PPort_).makeAndListen();
      } catch (IOException e) {
        logger_.error("Error when making peer: " + e);
        throw e;
      }

      bootstrapServerPeerAddress_ = new PeerAddress(Number160.ZERO,
          new InetSocketAddress(bootstrapServerAddress_, bootstrapTomP2PPort_));

      myPeer_.getConfiguration().setBehindFirewall(true);

      FutureDiscover discovery;
      discovery = myPeer_.discover().setPeerAddress(bootstrapServerPeerAddress_).start();
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

      FutureBootstrap bootstrap;

      bootstrap = myPeer_.bootstrap().setPeerAddress(bootstrapServerPeerAddress_).start();
      bootstrap.awaitUninterruptibly();
      if (!bootstrap.isSuccess()) {
        logger_.debug("Couldn't perform tomp2p bootstrap in iteration " + i + ": " +
            bootstrap.getFailedReason());
        try {
          Thread.sleep(300);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } else {
        success = true;
        break;
      }
    }
    if (!success) {
      String errMsg = "Couldn't perform tomp2p bootstrap!";
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

    resolver_ = new CachedAddressResolver(
        new HashAddressResolver(myCommAddress_, myPeer_));

    logger_.info("TomP2P initialization finished. My address is: " +
        myInetSocketAddress_ + ".");
  }

  @Override
  public void destroy() {
    checkSetUp();
    isTearingDown_.set(true);
    logger_.info("Starting tearDown procedure.");
    if (currentAddressDiscoverer_ != null) {
      currentAddressDiscoverer_.cancel();
      logger_.info("CurrentAddressDiscoverer canceled.");
    } else {
      logger_.info("CurrentAddressDiscoverer was not initialized.");
    }

    myPeer_.shutdown();
    resolver_ = null;
    myPeer_ = null;
    logger_.info("TomP2P peer has been shut down.");
  }

  @Override
  public CommAddressResolver getResolver() {
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
      if (isTearingDown_.get()) {
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
          logger_.warn(errMsg + " " + e);
          return;
        }
        logger_.info("Info about my address has been put to kademlia.");
        myInetSocketAddress_ = myInetSocketAddress;
      }
    }
  }
}
