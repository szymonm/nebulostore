package org.nebulostore.communication;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

import org.nebulostore.appcore.Message;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.bootstrap.BootstrapClient;
import org.nebulostore.communication.bootstrap.BootstrapServer;
import org.nebulostore.communication.bootstrap.BootstrapService;
import org.nebulostore.communication.gossip.GossipService;
import org.nebulostore.communication.gossip.GossipServiceFactory;
import org.nebulostore.communication.gossip.PeerSamplingGossipService;
import org.nebulostore.communication.socket.ListenerService;
import org.nebulostore.communication.socket.MessengerService;
import org.nebulostore.communication.socket.MessengerServiceFactory;
import org.nebulostore.peers.GenericConfiguration;

/* TODO(grzegorzmilka) Remove dependence on GenericConfiguration's xml config */
/**
 * Configuration Communication peer and its submodules.
 * @author Grzegorz Milka
 */
public class CommunicationPeerConfiguration extends GenericConfiguration {
  @Override
  protected final void configureAll() {
    bind(new TypeLiteral<BlockingQueue<Message>>() { })
      .annotatedWith(Names.named("BootstrapServerQueue")).
      toInstance(new LinkedBlockingQueue<Message>());

    configureLocalCommAddress();

    configureBootstrap();

    configureGossip();
    configureListenerService();
    configureMessengerService();
  }

  protected void configureBootstrap() {
    boolean isServer = config_.getString("communication.bootstrap.mode", "client").equals("server");
    bind(Boolean.class).annotatedWith(Names.named("IsServer")).toInstance(isServer);

    if (isServer) {
      bind(BootstrapService.class).to(BootstrapServer.class);
    } else {
      bind(BootstrapService.class).to(BootstrapClient.class);
    }
  }

  protected void configureLocalCommAddress() {
    bind(CommAddress.class).annotatedWith(Names.named("LocalCommAddress")).
      toInstance(new CommAddress(
            config_.getString("communication.comm-address", "")));
  }

  protected void configureGossip() {
    install(new FactoryModuleBuilder().implement(GossipService.class,
          PeerSamplingGossipService.class).build(GossipServiceFactory.class));
  }

  protected void configureListenerService() {
    bind(ListenerService.class);
  }

  protected void configureMessengerService() {
    install(new FactoryModuleBuilder().implement(MessengerService.class,
          MessengerService.class).build(MessengerServiceFactory.class));
  }
}
