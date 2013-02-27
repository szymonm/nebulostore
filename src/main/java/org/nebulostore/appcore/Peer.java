package org.nebulostore.appcore;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.api.DeleteNebuloObjectModule;
import org.nebulostore.api.GetNebuloObjectModule;
import org.nebulostore.api.WriteNebuloObjectModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.model.ObjectDeleter;
import org.nebulostore.appcore.model.ObjectGetter;
import org.nebulostore.appcore.model.ObjectWriter;
import org.nebulostore.broker.Broker;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.Dispatcher;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;
import org.nebulostore.replicator.Replicator;
import org.nebulostore.subscription.api.SimpleSubscriptionNotificationHandler;
import org.nebulostore.subscription.api.SubscriptionNotificationHandler;

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
  protected XMLConfiguration config_;
  protected Injector injector_;

  public void setConfiguration(XMLConfiguration config) {
    config_ = config;
  }

  @Inject
  public void setDependencies(@Named("DispatcherQueue") BlockingQueue<Message> dispatcherQueue,
      @Named("NetworkQueue") BlockingQueue<Message> networkQueue, AppKey appKey) {
    dispatcherInQueue_ = dispatcherQueue;
    networkInQueue_ = networkQueue;
    appKey_ = appKey;
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

  private Injector createInjector() {
    return Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(XMLConfiguration.class).toInstance(config_);

        bind(AppKey.class).toInstance(new AppKey(config_.getString("app-key", "")));
        // TODO(bolek) make it consistent with CommunicationPeer comm-address generation.
        bind(CommAddress.class).toInstance(
            new CommAddress(config_.getString("communication.comm-address", "")));

        bind(new TypeLiteral<BlockingQueue<Message>>() { })
          .annotatedWith(Names.named("NetworkQueue"))
          .toInstance(new LinkedBlockingQueue<Message>());
        bind(new TypeLiteral<BlockingQueue<Message>>() { })
          .annotatedWith(Names.named("DispatcherQueue"))
          .toInstance(new LinkedBlockingQueue<Message>());

        bind(ObjectGetter.class).to(GetNebuloObjectModule.class);
        bind(ObjectWriter.class).to(WriteNebuloObjectModule.class);
        bind(ObjectDeleter.class).to(DeleteNebuloObjectModule.class);

        bind(SubscriptionNotificationHandler.class).to(SimpleSubscriptionNotificationHandler.class);
      }
    });
  }

  protected void runPeer() {
    startPeer();
    putKey();
    finishPeer();
  }

  protected void putKey() {
    try {
      ApiFacade.putKey(appKey_);
    } catch (NebuloException e) {
      logger_.error(e);
    }
  }

  /**
   * Method that creates, connects and runs core application modules.
   */
  protected void startPeer() {
    // TODO(bolek): Remove ApiFacade.
    ApiFacade.initApi(dispatcherInQueue_);

    // Create dispatcher - outQueue will be passed to newly created tasks.
    dispatcherThread_ = new Thread(new Dispatcher(dispatcherInQueue_,
        networkInQueue_, injector_), "Dispatcher");

    // Create network module.
    try {
      CommunicationPeer peer = new CommunicationPeer(networkInQueue_, dispatcherInQueue_, config_);
      networkThread_ = new Thread(peer, "CommunicationPeer");
    } catch (NebuloException exception) {
      logger_.fatal("Error while creating CommunicationPeer");
      System.exit(1);
    }

    //TODO(bolek): Remove NetworkContext.
    //NetworkContext.getInstance().setAppKey(appKey_);
    GlobalContext.getInstance().setDispatcherQueue(dispatcherInQueue_);

    //Register instance in DHT
    /*GlobalContext.getInstance().setInstanceID(new InstanceID(CommunicationPeer.getPeerAddress()));
    dispatcherInQueue_.add(new JobInitMessage(new RegisterInstanceInDHTModule()));*/

    // Create Broker.
    String brokerJobId = CryptoUtils.getRandomId().toString();
    dispatcherInQueue_.add(new JobInitMessage(brokerJobId, new Broker(brokerJobId, true)));

    // Initialize Replicator.
    Replicator.setConfig(config_);

    // Run everything.
    networkThread_.start();
    dispatcherThread_.start();
    runInitialModules(dispatcherInQueue_);
  }

  protected void runInitialModules(BlockingQueue<Message> dispatcherQueue) {
    // Periodically checking asynchronous messages.
    /*IMessageGenerator retriveAMGenerator = new IMessageGenerator() {
      @Override
      public Message generate() {
        return new JobInitMessage(new RetrieveAsynchronousMessagesModule());
      }
    };
    PeriodicMessageSender sender = new PeriodicMessageSender(
        retriveAMGenerator, RETRIVE_ASYNCHRONOUS_MESSAGES_INTERVAL,
        dispatcherQueue);
    dispatcherQueue.add(new JobInitMessage(sender));

    // Add found peer to synchro peers.
    MessageGenerator addFoundSynchroPeer = new MessageGenerator() {
      @Override
      public Message generate() {
        return new JobInitMessage(new AddSynchroPeerModule());
      }
    };
    // TODO(bolek,szm): Temporarily disabled due to errors.
    //NetworkContext.getInstance().addContextChangeMessageGenerator(addFoundSynchroPeer);

    // Turning on statistics gossiping module.
    IMessageGenerator gossipingModuleGenerator = new IMessageGenerator() {
      @Override
      public Message generate() {
        return new JobInitMessage(new RandomPeersGossipingModule());
      }
    };
    PeriodicMessageSender gossiping = new PeriodicMessageSender(
        gossipingModuleGenerator, RandomPeersGossipingModule.INTERVAL,
        dispatcherQueue);
    dispatcherQueue.add(new JobInitMessage(gossiping));*/
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
