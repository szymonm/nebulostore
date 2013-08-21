package org.nebulostore.systest.performance;

import com.google.inject.Inject;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.TypeLiteral;

import org.nebulostore.communication.CommunicationPeerConfiguration;
import org.nebulostore.communication.gossip.GossipService;
import org.nebulostore.communication.gossip.GossipServiceFactory;
import org.nebulostore.communication.gossip.OneTimeUniformGossipService;
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
    GenericConfiguration genConf = new PerfTestingCommunicationPeerConfiguration();
    genConf.setXMLConfig(config_);
    install(genConf);
  }
}

/**
 * Configuration Communication peer and its submodules for performance peer.
 * @author Grzegorz Milka
 */
final class PerfTestingCommunicationPeerConfiguration extends CommunicationPeerConfiguration {
  @Override
  protected void configureGossip() {
    install(new FactoryModuleBuilder().implement(GossipService.class,
          OneTimeUniformGossipService.class).build(GossipServiceFactory.class));
  }
}
