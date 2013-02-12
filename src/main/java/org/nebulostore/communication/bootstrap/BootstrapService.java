package org.nebulostore.communication.bootstrap;

import java.io.IOException;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.address.CommAddressResolver;

/**
 * @author Grzegorz Milka
 */
public abstract class BootstrapService {
  protected int commCliPort_;
  /*
   * Port used by bootstrapping server for sending welcome message
   */
  protected int bootstrapPort_;
  /*
   * Port used by bootstrapping server for tomp2p
   */
  protected int bootstrapTomP2PPort_;
  /*
   * Port used for tomp2p by everyone else
   */
  protected int tomP2PPort_;

  public BootstrapService(
      int commCliPort,
      int bootstrapPort,
      int tomP2PPort,
      int bootstrapTomP2PPort) {
    commCliPort_ = commCliPort;
    bootstrapPort_ = bootstrapPort;
    tomP2PPort_ = tomP2PPort;
    bootstrapTomP2PPort_ = bootstrapTomP2PPort;
  }

  /**
   * Return CommAddress of BootstrapServer.
   */
  public abstract CommAddress getBootstrapCommAddress();

  public abstract CommAddressResolver getResolver();

  public abstract void startUpService() throws IOException;
  public abstract void shutdownService();
}
