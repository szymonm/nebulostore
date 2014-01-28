package org.nebulostore.systest.performance;

import java.util.concurrent.ExecutorService;

import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

import org.apache.commons.configuration.XMLConfiguration;
import org.nebulostore.broker.AlwaysAcceptingBroker;
import org.nebulostore.broker.Broker;
import org.nebulostore.communication.CommunicationPeerConfiguration;
import org.nebulostore.communication.gossip.GossipService;
import org.nebulostore.communication.gossip.GossipServiceFactory;
import org.nebulostore.communication.gossip.OneTimeUniformGossipService;
import org.nebulostore.newcommunication.CommunicationFacadeAdapterConfiguration;
import org.nebulostore.newcommunication.CommunicationFacadeConfiguration;
import org.nebulostore.newcommunication.peerdiscovery.OneTimeUniformGossipPeerDiscovery;
import org.nebulostore.newcommunication.peerdiscovery.PeerDiscovery;
import org.nebulostore.newcommunication.peerdiscovery.PeerDiscoveryFactory;
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
    if (config_.getString("communication.comm-module", "").equals("communication")) {
      genConf = new PerfTestingCommunicationPeerConfiguration();
    } else {
      genConf = new PerfTestingCommunicationFacadeAdapterConfiguration();
    }
    genConf.setXMLConfig(config_);
    install(genConf);
  }

  @Override
  protected void configureBroker() {
    bind(Broker.class).to(AlwaysAcceptingBroker.class).in(Scopes.SINGLETON);
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

final class PerfTestingCommunicationFacadeConfiguration extends CommunicationFacadeConfiguration {

  public PerfTestingCommunicationFacadeConfiguration(XMLConfiguration xmlConfig) {
    super(xmlConfig);
  }

  @Override
  protected void configurePeerDiscovery() {
    boolean isServer = xmlConfig_.getString("communication.bootstrap.mode", "client").equals(
        "server");
    bind(Boolean.class).annotatedWith(
        Names.named("communication.boostrap.is-server")).toInstance(isServer);

    bind(ExecutorService.class).annotatedWith(
        Names.named("communication.peerdiscovery.service-executor")).toInstance(serviceExecutor_);

    install(new FactoryModuleBuilder().implement(PeerDiscovery.class,
        OneTimeUniformGossipPeerDiscovery.class).build(PeerDiscoveryFactory.class));
  }
}

final class PerfTestingCommunicationFacadeAdapterConfiguration
                                                  extends CommunicationFacadeAdapterConfiguration {
  @Override
  protected CommunicationFacadeConfiguration createCommunicationFacadeConfiguration() {
    return new PerfTestingCommunicationFacadeConfiguration(config_);
  }
}
