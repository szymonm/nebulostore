package org.nebulostore.appcore;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.appcore.context.NebuloContext;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.model.NebuloObject;
import org.nebulostore.broker.Broker;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.Dispatcher;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;
import org.nebulostore.replicator.Replicator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is a regular peer with full functionality.
 * To create a different peer, subclass Peer and set its class name in configuration.
 * @author bolek
 */
public class Peer implements Runnable {
  private static Logger logger_ = Logger.getLogger(Peer.class);
  protected static final Long RETRIVE_ASYNCHRONOUS_MESSAGES_INTERVAL = 2000L;

  protected BlockingQueue<Message> networkInQueue_;
  protected BlockingQueue<Message> dispatcherInQueue_;
  protected Thread dispatcherThread_;
  protected Thread networkThread_;
  protected AppKey appKey_;
  protected XMLConfiguration config_;
  protected Injector injector_;

  public void setConfiguration(XMLConfiguration config) {
    config_ = config;
  }

  @Override
  public void run() {
    checkNotNull(config_);
    String appKey = config_.getString("app-key", "");
    if (appKey.isEmpty()) {
      appKey_ = new AppKey(CryptoUtils.getRandomId());
    } else {
      appKey_ = new AppKey(new BigInteger(appKey));
    }
    injector_ = Guice.createInjector(new NebuloContext(appKey_, config_));
    runPeer();
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
    networkInQueue_ = new LinkedBlockingQueue<Message>();
    dispatcherInQueue_ = new LinkedBlockingQueue<Message>();
    ApiFacade.initApi(dispatcherInQueue_);
    NebuloObject.initObjectApi(dispatcherInQueue_);

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
      // TODO: Make CommunicationPeer exit cleanly.
      // networkThread_.join();
      dispatcherThread_.join();
    } catch (InterruptedException exception) {
      logger_.fatal("Interrupted");
      return;
    }
  }

  public void quitNebuloStore() {
    dispatcherInQueue_.add(new KillDispatcherMessage());
  }
}
