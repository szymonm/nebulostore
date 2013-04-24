package org.nebulostore.communication.address;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import org.apache.log4j.Logger;
import org.nebulostore.communication.dht.KeyDHT;

/**
 * TomP2P's kademlia-like implementation of persistent addressing service.
 *
 * @author Grzegorz Milka
 */
public abstract class TomP2PPeer implements PersistentAddressingPeer {
  protected Logger logger_;

  protected Peer myPeer_;
  protected CommAddressResolver resolver_;
  protected int bootstrapTomP2PPort_ = -1;
  protected int tomP2PPort_ = -1;
  protected int commCliPort_ = -1;

  protected InetSocketAddress myInetSocketAddress_;

  protected String bootstrapServerAddress_;
  protected PeerAddress bootstrapServerPeerAddress_;

  protected CommAddress myCommAddress_;

  protected AtomicBoolean isTearingDown_ = new AtomicBoolean(false);

  public TomP2PPeer() {
    logger_ = Logger.getLogger(this.getClass());
  }

  @Inject
  @Override
  public void setBootstrapDHTPort(@Named("BootstrapAddressingPort") int port) {
    bootstrapTomP2PPort_ = port;
  }

  @Inject
  @Override
  public void setDHTPort(@Named("LocalAddressingPort") int port) {
    tomP2PPort_ = port;
  }

  @Inject
  @Override
  public void setCommPort(@Named("LocalCommPort") int port) {
    commCliPort_ = port;
  }

  @Inject
  @Override
  public void setBootstrapServerAddress(@Named("BootstrapServerAddress")
      String bootstrapServerAddress) {
    if (bootstrapServerAddress == null) {
      throw new IllegalArgumentException("Bootstrap address can not be null");
    }
    bootstrapServerAddress_ = bootstrapServerAddress;
  }

  @Inject
  @Override
  public void setMyCommAddress(@Named("LocalCommAddress") CommAddress myCommAddress) {
    if (myCommAddress == null) {
      throw new IllegalArgumentException("myCommAddress can not be null");
    }
    myCommAddress_ = myCommAddress;
  }

  public Peer getPeer() {
    return myPeer_;
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
    Number160 addressKey = KeyDHT.combine(
        KeyDHT.COMMUNICATION_KEY,
        new Number160(myCommAddress_.hashCode()));
    FutureDHT putFuture = myPeer_.put(addressKey).
      setData(new Data(address)).start().awaitUninterruptibly();
    if (!putFuture.isSuccess()) {
      throw new IOException("Couldn't upload address to kademlia. " +
          putFuture.getFailedReason());
    }
  }

  protected void checkSetUp() {
    if (myPeer_ == null) {
      throw new IllegalStateException("Peer has not been set up.");
    }
  }
}
