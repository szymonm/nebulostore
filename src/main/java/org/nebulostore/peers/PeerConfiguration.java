package org.nebulostore.peers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import org.apache.commons.configuration.XMLConfiguration;
import org.nebulostore.api.DeleteNebuloObjectModule;
import org.nebulostore.api.GetNebuloObjectModule;
import org.nebulostore.api.WriteNebuloObjectModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.addressing.AppKey;
import org.nebulostore.appcore.model.ObjectDeleter;
import org.nebulostore.appcore.model.ObjectGetter;
import org.nebulostore.appcore.model.ObjectWriter;
import org.nebulostore.broker.AlwaysAcceptingBroker;
import org.nebulostore.broker.Broker;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.gossip.GossipService;
import org.nebulostore.communication.gossip.PeerSamplingGossipService;
import org.nebulostore.subscription.api.SimpleSubscriptionNotificationHandler;
import org.nebulostore.subscription.api.SubscriptionNotificationHandler;
import org.nebulostore.timer.Timer;
import org.nebulostore.timer.TimerImpl;

/**
 * Configuration (all dependencies and constants) of a regular Nebulostore peer.
 * @author Bolek Kulbabinski
 */
public class PeerConfiguration extends GenericConfiguration {

  @Override
  protected void configureAll() {
    bind(XMLConfiguration.class).toInstance(config_);

    bind(AppKey.class).toInstance(new AppKey(config_.getString("app-key", "")));
    bind(CommAddress.class).toInstance(
        new CommAddress(config_.getString("communication.comm-address", "")));

    bind(new TypeLiteral<BlockingQueue<Message>>() { })
      .annotatedWith(Names.named("NetworkQueue")).toInstance(new LinkedBlockingQueue<Message>());
    bind(new TypeLiteral<BlockingQueue<Message>>() { })
      .annotatedWith(Names.named("DispatcherQueue")).toInstance(new LinkedBlockingQueue<Message>());

    bind(ObjectGetter.class).to(GetNebuloObjectModule.class);
    bind(ObjectWriter.class).to(WriteNebuloObjectModule.class);
    bind(ObjectDeleter.class).to(DeleteNebuloObjectModule.class);

    bind(SubscriptionNotificationHandler.class).to(SimpleSubscriptionNotificationHandler.class);

    bind(Timer.class).to(TimerImpl.class);

    configureCommunicationPeer();
    configureBroker();
    configurePeer();
  }

  protected void configurePeer() {
    bind(AbstractPeer.class).to(Peer.class);
  }

  protected void configureCommunicationPeer() {
    //TODO(bolek): Move it to CommunicationPeer's config module.
    bind(GossipService.class).to(PeerSamplingGossipService.class);
  }

  protected void configureBroker() {
    bind(Broker.class).to(AlwaysAcceptingBroker.class).in(Scopes.SINGLETON);
  }
}
