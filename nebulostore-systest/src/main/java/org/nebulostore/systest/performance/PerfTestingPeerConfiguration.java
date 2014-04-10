package org.nebulostore.systest.performance;

import org.nebulostore.communication.CommunicationFacadeAdapterConfiguration;

import org.nebulostore.peers.AbstractPeer;
import org.nebulostore.peers.GenericConfiguration;
import org.nebulostore.systest.TestingPeerConfiguration;

/**
 * Configuration for PerfTestingPeer.
 * @author Bolek Kulbabinski
 */
public class PerfTestingPeerConfiguration extends TestingPeerConfiguration {
  @Override
  protected void configurePeer() {
    bind(AbstractPeer.class).to(PerfTestingPeer.class);
  }

  @Override
  protected void configureCommunicationPeer() {
    GenericConfiguration genConf;
    genConf = new CommunicationFacadeAdapterConfiguration();
    genConf.setXMLConfig(config_);
    install(genConf);
  }

}
