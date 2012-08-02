package org.nebulostore.communication.bootstrap;

import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;

/**
 * @author Grzegorz Milka
 */
public abstract class BootstrapService {
  protected static final int COMM_CLI_PORT = CommunicationPeer.COMM_CLI_PORT;
  protected static final int BOOTSTRAP_PORT = 9989;
  protected static final int TOMP2P_PORT = 9991;
  protected int commCliPort_ = COMM_CLI_PORT;
  protected int bootstrapPort_ = BOOTSTRAP_PORT;
  protected int tomp2pPort_ = TOMP2P_PORT;

  public BootstrapService(int commCliPort) {
    commCliPort_ = commCliPort;
  }

  public abstract CommAddress getBootstrapCommAddress();

  public abstract CommAddressResolver getResolver();
}
