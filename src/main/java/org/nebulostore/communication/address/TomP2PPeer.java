package org.nebulostore.communication.address;

import java.io.IOException;
import java.net.InetSocketAddress;

import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import org.apache.log4j.Logger;
import org.nebulostore.communication.CommunicationPeer;

/**
 * TomP2P's kademlia based implementation of persistent addressing service.
 *
 * @author Grzegorz Milka
 */
//NOTE-GM Perhaps add TomP2PBuilder (Builder pattern)
public abstract class TomP2PPeer implements IPersistentAddressingPeer {
  protected static Logger logger_;

  protected static final int COMM_CLI_PORT = CommunicationPeer.commCliPort_;
  protected static final int BOOTSTRAP_TOMP2P_PORT = 9991;
  protected static final int TOMP2P_PORT = COMM_CLI_PORT + 100;

  protected Peer myPeer_;
  protected ICommAddressResolver resolver_;
  protected int bootstrapTomP2PPort_ = BOOTSTRAP_TOMP2P_PORT;
  protected int tomp2pPort_ = TOMP2P_PORT;
  protected int commCliPort_ = COMM_CLI_PORT;

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
  public void setDHTPort(int port) {
    tomp2pPort_ = port;
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
  public ICommAddressResolver getResolver() {
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
