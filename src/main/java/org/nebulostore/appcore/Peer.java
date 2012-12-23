package org.nebulostore.appcore;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.appcore.context.NebuloContext;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.async.AddSynchroPeerModule;
import org.nebulostore.broker.Broker;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.Dispatcher;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;
import org.nebulostore.networkmonitor.NetworkContext;
import org.nebulostore.timer.MessageGenerator;

/**
 * @author marcin This is the entry point for a regular peer with full
 *         functionality.
 */
public class Peer {
  static final Long RETRIVE_ASYNCHRONOUS_MESSAGES_INTERVAL = 2000L;

  private static Logger logger_ = Logger.getLogger(Peer.class);
  protected static BlockingQueue<Message> networkInQueue_;
  protected static BlockingQueue<Message> dispatcherInQueue_;
  private static Thread dispatcherThread_;
  private static Thread networkThread_;

  protected Peer() {
  }

  /**
   * @param args
   *          Command line arguments.
   */
  public static void main(String[] args) {

    DOMConfigurator.configure("resources/conf/log4j.xml");
    Injector injector = Guice.createInjector(new NebuloContext());

    BigInteger appKey = BigInteger.ZERO;
    if (args.length < 1) {
      // Random AppKey if not provided.
      appKey = CryptoUtils.getRandomId();
    } else {
      appKey = new BigInteger(args[0]);
    }
    runPeer(new AppKey(appKey), injector);
  }

  public static void runPeer(AppKey appKey, Injector injector) {
    startPeer(appKey, injector);

    try {
      ApiFacade.putKey(appKey);
    } catch (NebuloException e) {
      logger_.error(e);
    }

    finishPeer();
  }

  protected static void startPeer(AppKey appKey, Injector injector) {
    networkInQueue_ = new LinkedBlockingQueue<Message>();
    dispatcherInQueue_ = new LinkedBlockingQueue<Message>();
    ApiFacade.initApi(dispatcherInQueue_);
    NebuloObject.initObjectApi(dispatcherInQueue_);

    // Create dispatcher - outQueue will be passed to newly created tasks.
    dispatcherThread_ = new Thread(new Dispatcher(dispatcherInQueue_,
        networkInQueue_, injector), "Dispatcher");
    // Create network module.
    try {
      networkThread_ = new Thread(new CommunicationPeer(networkInQueue_,
          dispatcherInQueue_), "CommunicationPeer");
    } catch (NebuloException exception) {
      logger_.fatal("Error while creating CommunicationPeer");
      exception.printStackTrace();
      System.exit(-1);
    }

    NetworkContext.getInstance().setAppKey(appKey);
    GlobalContext.getInstance().setDispatcherQueue(dispatcherInQueue_);

    //Register instance in DHT
    /*GlobalContext.getInstance().setInstanceID(new InstanceID(CommunicationPeer.getPeerAddress()));
    dispatcherInQueue_.add(new JobInitMessage(new RegisterInstanceInDHTModule()));*/

    // Create Broker.
    String brokerJobId = CryptoUtils.getRandomId().toString();
    dispatcherInQueue_.add(new JobInitMessage(brokerJobId, new Broker(brokerJobId, true)));

    // Run everything.
    networkThread_.start();
    dispatcherThread_.start();
    runInitialModules(dispatcherInQueue_);
  }

  protected static void runInitialModules(BlockingQueue<Message> dispatcherQueue) {
    // TODO(szm) move it somewhere

    /* Periodically checking asynchronous messages. */
//    IMessageGenerator retriveAMGenerator = new IMessageGenerator() {
//      @Override
//      public Message generate() {
//        return new JobInitMessage(new RetrieveAsynchronousMessagesModule());
//      }
//    };
//    PeriodicMessageSender sender = new PeriodicMessageSender(
//        retriveAMGenerator, RETRIVE_ASYNCHRONOUS_MESSAGES_INTERVAL,
//        dispatcherQueue);
//    dispatcherQueue.add(new JobInitMessage(sender));

    /* Adds found peer to synchro peers */
    MessageGenerator addFoundSynchroPeer = new MessageGenerator() {
      @Override
      public Message generate() {
        return new JobInitMessage(new AddSynchroPeerModule());
      }
    };
    // TODO(bolek,szm): Temporarily disabled due to errors.
    //NetworkContext.getInstance().addContextChangeMessageGenerator(addFoundSynchroPeer);

    /* Turning on statistics gossiping module */
//    IMessageGenerator gossipingModuleGenerator = new IMessageGenerator() {
//      @Override
//      public Message generate() {
//        return new JobInitMessage(new RandomPeersGossipingModule());
//      }
//    };
//    PeriodicMessageSender gossiping = new PeriodicMessageSender(
//        gossipingModuleGenerator, RandomPeersGossipingModule.INTERVAL,
//        dispatcherQueue);
//    dispatcherQueue.add(new JobInitMessage(gossiping));
  }

  protected static void finishPeer() {
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

  public static void quitNebuloStore() {
    dispatcherInQueue_.add(new KillDispatcherMessage());
  }
}
