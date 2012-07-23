package org.nebulostore.communication.bootstrap;

import org.apache.log4j.Logger;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.bootstrap.BootstrapMessage;
import org.nebulostore.communication.bootstrap.CommAddressResolver;

/**
 * @author Grzegorz Milka
 */
public abstract class BootstrapService {
  protected static final int COMM_CLI_PORT_ = 9987;
  protected static final int BOOTSTRAP_PORT_ = 9989;
  protected static final int TOMP2P_PORT_ = 9991;
  protected int commCliPort_ = COMM_CLI_PORT_; 
  protected int bootstrapPort_ = BOOTSTRAP_PORT_;
  protected int tomp2pPort_ = TOMP2P_PORT_; 

  public BootstrapService(int commCliPort) {
    commCliPort_ = commCliPort;
  }

  public abstract CommAddress getBootstrapCommAddress();

  public abstract CommAddressResolver getResolver();
}
