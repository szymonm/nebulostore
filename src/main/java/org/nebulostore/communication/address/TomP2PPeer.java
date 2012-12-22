package org.nebulostore.communication.address;

import java.io.IOException;
import java.net.InetSocketAddress;

import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import org.apache.log4j.Logger;

/**
 * TomP2P's kademlia based implementation of persistent addressing service.
 *
 * @author Grzegorz Milka
 */
//NOTE-GM Perhaps add TomP2PBuilder (Builder pattern)
public abstract class TomP2PPeer implements PersistentAddressingPeer {
  protected static Logger logger_;

  protected Peer myPeer_;
  protected CommAddressResolver resolver_;
  protected int bootstrapTomP2PPort_ = -1;
  protected int tomP2PPort_ = -1;
  protected int commCliPort_ = -1;

  protected InetSocketAddress myInetSocketAddress_;

  protected String bootstrapServerAddress_;
  protected PeerAddress bootstrapServerPeerAddress_;

  protected CommAddress myCommAddress_;

  protected Boolean isTearingDown_ = false;

  public TomP2PPeer() {
    if (logger_ == null) {
      logger_ = Logger.getLogger(this.getClass());
    }
  }

  @Override
  public void setBootstrapDHTPort(int port) {
    bootstrapTomP2PPort_ = port;
  }

  @Override
  public void setDHTPort(int port) {
    tomP2PPort_ = port;
  }

  @Override
  public void setCommPort(int port) {
    commCliPort_ = port;
  }

  @Override
  public void setBootstrapServerAddress(String bootstrapServerAddress) {
    if (bootstrapServerAddress == null)
      throw new IllegalArgumentException("Bootstrap address can not be null");
    bootstrapServerAddress_ = bootstrapServerAddress;
  }

  @Override
  public void setMyCommAddress(CommAddress myCommAddress) {
    if (myCommAddress == null)
      throw new IllegalArgumentException("myCommAddress can not be null");
    myCommAddress_ = myCommAddress;
  }

  @Override
  public CommAddressResolver getResolver() {
    checkSetUp();
    return resolver_;
  }

  @Override
  /**
   * Uploads address returned by getCurrentInetSocketAddress to DHT.
   */
  public void uploadCurrentInetSocketAddress() throws IOException {
    uploadCurrentInetSocketAddress(getCurrentInetSocketAddress());
  }

  @Override
  public void uploadCurrentInetSocketAddress(InetSocketAddress address)
    throws IOException {
    checkSetUp();
    FutureDHT putFuture = myPeer_.put(new Number160(myCommAddress_.hashCode())).
      setData(new Data(address)).start().awaitUninterruptibly();
    if (!putFuture.isSuccess()) {
      throw new IOException("Couldn't upload address to kademlia. " +
          putFuture.getFailedReason());
    }
  }

  protected void checkSetUp() throws IllegalStateException {
    if (myPeer_ == null) {
      throw new IllegalStateException("Peer has not been set up.");
    }
  }
}
