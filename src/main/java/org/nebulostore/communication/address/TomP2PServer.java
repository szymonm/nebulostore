package org.nebulostore.communication.address;

import java.io.IOException;
import java.net.InetSocketAddress;

import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;

/**
 * TomP2P's kademlia based implementation of persistent addressing service.
 *
 * @author Grzegorz Milka
 */
//NOTE-GM Perhaps add TomP2PBuilder (Builder pattern)
public final class TomP2PServer extends TomP2PPeer {
  private InetSocketAddress myInetSocketAddress_;

  public TomP2PServer() {
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

    try {
      uploadCurrentInetSocketAddress();
    } catch (IOException e) {
      logger_.warn(e.getMessage() + e.getCause().toString());
    }

    resolver_ = new HashAddressResolver(myCommAddress_, myPeer_);

    logger_.info("TomP2PServer initialization finished. My address is: " +
        bootstrapServerAddress_ + ".");
  }

  @Override
  public void tearDown() {
    checkSetUp();
    synchronized (isTearingDown_) {
      isTearingDown_ = true;
    }
    logger_.info("Starting tearDown procedure.");
    myPeer_.shutdown();
    resolver_ = null;
    myPeer_ = null;
    logger_.info("TomP2P peer has been shut down.");
  }

  @Override
  public InetSocketAddress getCurrentInetSocketAddress() throws IOException {
    checkSetUp();
    return new InetSocketAddress(bootstrapServerAddress_, commCliPort_);
  }
}
