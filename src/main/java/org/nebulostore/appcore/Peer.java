package org.nebulostore.appcore;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.api.DeleteNebuloObjectModule;
import org.nebulostore.api.GetNebuloObjectModule;
import org.nebulostore.api.PutKeyModule;
import org.nebulostore.api.WriteNebuloObjectModule;
import org.nebulostore.appcore.addressing.AppKey;
import org.nebulostore.appcore.addressing.ReplicationGroup;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.model.ObjectDeleter;
import org.nebulostore.appcore.model.ObjectGetter;
import org.nebulostore.appcore.model.ObjectWriter;
import org.nebulostore.async.AddSynchroPeerModule;
import org.nebulostore.async.RetrieveAsynchronousMessagesModule;
import org.nebulostore.broker.AlwaysAcceptingBroker;
import org.nebulostore.broker.Broker;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.gossip.GossipService;
import org.nebulostore.communication.gossip.PeerGossipService;
import org.nebulostore.dispatcher.Dispatcher;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;
import org.nebulostore.networkmonitor.NetworkContext;
import org.nebulostore.replicator.Replicator;
import org.nebulostore.subscription.api.SimpleSubscriptionNotificationHandler;
import org.nebulostore.subscription.api.SubscriptionNotificationHandler;
import org.nebulostore.timer.MessageGenerator;
import org.nebulostore.timer.PeriodicMessageSender;

/**
 * This is a regular peer with full functionality. It creates, connects and runs all modules.
 * To create a different peer, subclass Peer and set its class name in configuration.
 * @author Bolek Kulbabinski
 */
public class Peer implements Runnable {
  private static Logger logger_ = Logger.getLogger(Peer.class);
  //protected static final Long RETRIVE_ASYNCHRONOUS_MESSAGES_INTERVAL = 2000L;

  protected Thread dispatcherThread_;
  protected Thread networkThread_;
  protected BlockingQueue<Message> dispatcherInQueue_;
  protected BlockingQueue<Message> networkInQueue_;

  protected AppKey appKey_;
  protected Broker broker_;
  protected XMLConfiguration config_;
  protected Injector injector_;
  protected CommAddress commAddress_;

  public void setConfiguration(XMLConfiguration config) {
    config_ = config;
  }

  @Inject
  public void setDependencies(@Named("DispatcherQueue") BlockingQueue<Message> dispatcherQueue,
                              @Named("NetworkQueue") BlockingQueue<Message> networkQueue,
                              Broker broker,
                              AppKey appKey,
                              CommAddress commAddress) {
    dispatcherInQueue_ = dispatcherQueue;
    networkInQueue_ = networkQueue;
    broker_ = broker;
    appKey_ = appKey;
    commAddress_ = commAddress;
  }

  @Override
  public final void run() {
    injector_ = createInjector();
    injector_.injectMembers(this);
    runPeer();
  }

  public void quitNebuloStore() {
    if (networkInQueue_ != null)
      networkInQueue_.add(new EndModuleMessage());
    if (dispatcherInQueue_ != null)
      dispatcherInQueue_.add(new KillDispatcherMessage());
  }

  protected Injector createInjector() {
    return Guice.createInjector(new PeerGuiceModule());
  }

  /**
   * Standard dependency configuration.
   * @author Bolek Kulbabinski
   */
  protected class PeerGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(XMLConfiguration.class).toInstance(config_);

      bind(AppKey.class).toInstance(new AppKey(config_.getString("app-key", "")));
      bind(CommAddress.class).toInstance(
          new CommAddress(config_.getString("communication.comm-address", "")));

      bind(new TypeLiteral<BlockingQueue<Message>>() { })
        .annotatedWith(Names.named("NetworkQueue")).toInstance(new LinkedBlockingQueue<Message>());
      bind(new TypeLiteral<BlockingQueue<Message>>() { })
        .annotatedWith(Names.named("DispatcherQueue")).toInstance(
            new LinkedBlockingQueue<Message>());

      bind(ObjectGetter.class).to(GetNebuloObjectModule.class);
      bind(ObjectWriter.class).to(WriteNebuloObjectModule.class);
      bind(ObjectDeleter.class).to(DeleteNebuloObjectModule.class);

      bind(SubscriptionNotificationHandler.class).to(SimpleSubscriptionNotificationHandler.class);
      configureCommunicationPeer();
      configureBroker();
    }

    protected void configureCommunicationPeer() {
      bind(GossipService.class).to(PeerGossipService.class);
    }

    protected void configureBroker() {
      bind(Broker.class).to(AlwaysAcceptingBroker.class).in(Scopes.SINGLETON);
    }
  }

  protected void runPeer() {
    startPeer();
    runBroker();
    runAsyncModules();
    putKey(appKey_);
    finishPeer();
  }

  protected void putKey(AppKey appKey) {
    // TODO(bolek): This should be part of broker.
    PutKeyModule module = new PutKeyModule(new ReplicationGroup(new CommAddress[]{commAddress_},
        new BigInteger("0"), new BigInteger("1000000")), dispatcherInQueue_);
    try {
      module.getResult(30);
    } catch (NebuloException exception) {
      logger_.error("Unable to execute PutKeyModule!", exception);
    }
  }

  /**
   * Method that creates, connects and runs Dispatcher and Communication modules.
   */
  protected void startPeer() {
    Dispatcher dispatcher = new Dispatcher(dispatcherInQueue_, networkInQueue_, injector_);
    dispatcherThread_ = new Thread(dispatcher, "Dispatcher");

    CommunicationPeer peer = new CommunicationPeer(networkInQueue_, dispatcherInQueue_);
    injector_.injectMembers(peer);
    networkThread_ = new Thread(peer, "CommunicationPeer");

    // TODO(szymonmatejczyk): Remove GlobalContext.
    GlobalContext.getInstance().setDispatcherQueue(dispatcherInQueue_);
    NetworkContext.getInstance().setCommAddress(commAddress_);

    //Register instance in DHT
    /*GlobalContext.getInstance().setInstanceID(new InstanceID(CommunicationPeer.getPeerAddress()));
    dispatcherInQueue_.add(new JobInitMessage(new RegisterInstanceInDHTModule()));*/

    // Initialize Replicator.
    Replicator.setConfig(config_);
    Replicator.setAppKey(appKey_);

    // Run everything.
    networkThread_.start();
    dispatcherThread_.start();
  }

  protected void runBroker() {
    broker_.runThroughDispatcher();
  }

  protected void runAsyncModules() {
    // Periodically checking asynchronous messages.
    MessageGenerator retrieveAMGenerator = new MessageGenerator() {
      @Override
      public Message generate() {
        return new JobInitMessage(new RetrieveAsynchronousMessagesModule());
      }
    };
    PeriodicMessageSender sender = new PeriodicMessageSender(
        retrieveAMGenerator, RetrieveAsynchronousMessagesModule.INTERVAL,
        dispatcherInQueue_);
    dispatcherInQueue_.add(new JobInitMessage(sender));

    // Add found peer to synchro peers.
    MessageGenerator addFoundSynchroPeer = new MessageGenerator() {
      @Override
      public Message generate() {
        return new JobInitMessage(new AddSynchroPeerModule());
      }
    };

    // TODO(bolek,szm): Temporarily disabled due to errors.
    NetworkContext.getInstance().addContextChangeMessageGenerator(addFoundSynchroPeer);

    // Turning on statistics gossiping module.
    /*IMessageGenerator gossipingModuleGenerator = new IMessageGenerator() {
      @Override
      public Message generate() {
        return new JobInitMessage(new RandomPeersGossipingModule());
      }
    };
    PeriodicMessageSender gossiping = new PeriodicMessageSender(
        gossipingModuleGenerator, RandomPeersGossipingModule.INTERVAL,
        dispatcherQueue);
    dispatcherQueue.add(new JobInitMessage(gossiping));
    */
  }

  protected void finishPeer() {
    // Wait for threads to finish execution.
    try {
      networkThread_.join();
      dispatcherThread_.join();
    } catch (InterruptedException exception) {
      logger_.fatal("Interrupted");
      return;
    }
  }
}
