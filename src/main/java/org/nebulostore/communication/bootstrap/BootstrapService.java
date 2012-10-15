package org.nebulostore.communication.bootstrap;

import java.io.IOException;

import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.address.ICommAddressResolver;

/**
 * @author Grzegorz Milka
 */
public abstract class BootstrapService {
  protected static final int COMM_CLI_PORT = CommunicationPeer.commCliPort_;
  /*
   * Port used by bootstrapping server for sending welcome message
   */
  protected static final int BOOTSTRAP_PORT = 9989;
  /*
   * Port used by bootstrapping server for tomp2p
   */
  protected static final int BOOTSTRAP_TOMP2P_PORT = 9991;
  /*
   * Port used by bootstrapping server for tomp2p by everyone else
   */
  protected static final int TOMP2P_PORT = COMM_CLI_PORT + 100;
  protected int commCliPort_ = COMM_CLI_PORT;
  protected int bootstrapPort_ = BOOTSTRAP_PORT;
  protected int bootstrapTomP2PPort_ = BOOTSTRAP_TOMP2P_PORT;
  protected int tomp2pPort_ = TOMP2P_PORT;

  public BootstrapService(int commCliPort) {
    commCliPort_ = commCliPort;
  }

  public abstract CommAddress getBootstrapCommAddress();

  public abstract ICommAddressResolver getResolver();

  public abstract void startUpService() throws IOException;
  public abstract void shutdownService();
}
