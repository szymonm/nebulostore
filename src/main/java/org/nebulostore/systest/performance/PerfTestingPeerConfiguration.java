package org.nebulostore.systest.performance;

import org.nebulostore.appcore.AbstractPeer;
import org.nebulostore.communication.gossip.GossipService;
import org.nebulostore.communication.gossip.OneTimeUniformGossipService;
import org.nebulostore.systest.TestingPeer;
import org.nebulostore.systest.TestingPeerConfiguration;

/**
 * Configuration for PerfTestingPeer.
 * @author Bolek Kulbabinski
 */
public class PerfTestingPeerConfiguration extends TestingPeerConfiguration {
  @Override
  protected void configurePeer() {
    bind(AbstractPeer.class).to(TestingPeer.class);
  }

  @Override
  protected void configureCommunicationPeer() {
    bind(GossipService.class).to(OneTimeUniformGossipService.class);
  }
}
