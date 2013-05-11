package org.nebulostore.peers;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.api.PutKeyModule;
import org.nebulostore.appcore.EndModuleMessage;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.addressing.AppKey;
import org.nebulostore.appcore.addressing.ReplicationGroup;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.async.AddSynchroPeerModule;
import org.nebulostore.async.RetrieveAsynchronousMessagesModule;
import org.nebulostore.broker.Broker;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.CommunicationPeerFactory;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.dispatcher.Dispatcher;
import org.nebulostore.dispatcher.JobInitMessage;
import org.nebulostore.networkmonitor.NetworkContext;
import org.nebulostore.replicator.Replicator;
import org.nebulostore.timer.MessageGenerator;
import org.nebulostore.timer.Timer;

/**
 * This is a regular peer with full functionality. It creates, connects and runs all modules.
 * To create a different peer, subclass Peer and set its class name in configuration.
 * @author Bolek Kulbabinski
 */
public class Peer extends AbstractPeer {
  private static Logger logger_ = Logger.getLogger(Peer.class);

  protected Thread dispatcherThread_;
  protected Thread networkThread_;
  protected BlockingQueue<Message> dispatcherInQueue_;
  protected BlockingQueue<Message> networkInQueue_;

  protected AppKey appKey_;
  protected Broker broker_;
  protected XMLConfiguration config_;
  protected Injector injector_;
  protected CommAddress commAddress_;
  protected Timer peerTimer_;

  private CommunicationPeer commPeer_;
  private CommunicationPeerFactory commPeerFactory_;

  @Inject
  public void setConfiguration(XMLConfiguration config) {
    config_ = config;
  }

  @Inject
  public void setDependencies(@Named("DispatcherQueue") BlockingQueue<Message> dispatcherQueue,
                              @Named("NetworkQueue") BlockingQueue<Message> networkQueue,
                              Broker broker,
                              AppKey appKey,
                              CommAddress commAddress,
                              CommunicationPeerFactory commPeerFactory,
                              Timer timer,
                              Injector injector) {
    dispatcherInQueue_ = dispatcherQueue;
    networkInQueue_ = networkQueue;
    broker_ = broker;
    appKey_ = appKey;
    commAddress_ = commAddress;
    commPeerFactory_ = commPeerFactory;
    peerTimer_ = timer;
    injector_ = injector;
  }

  @Override
  public final void run() {
    runPeer();
  }

  public void quitNebuloStore() {
    if (networkInQueue_ != null) {
      networkInQueue_.add(new EndModuleMessage());
    }
    if (dispatcherInQueue_ != null) {
      dispatcherInQueue_.add(new EndModuleMessage());
    }
  }

  protected void runPeer() {
    initPeer();
    runBroker();
    runAsyncModules();
    startPeer();
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
  protected void initPeer() {
    Dispatcher dispatcher = new Dispatcher(dispatcherInQueue_, networkInQueue_, injector_);
    dispatcherThread_ = new Thread(dispatcher, "Dispatcher");

    commPeer_ = commPeerFactory_.newCommunicationPeer(networkInQueue_, dispatcherInQueue_);
    networkThread_ = new Thread(commPeer_, "CommunicationPeer");

    NetworkContext.getInstance().setCommAddress(commAddress_);
    NetworkContext.getInstance().setDispatcherQueue(dispatcherInQueue_);

    //Register instance in DHT
    /*GlobalContext.getInstance().setInstanceID(new InstanceID(CommunicationPeer.getPeerAddress()));
    dispatcherInQueue_.add(new JobInitMessage(new RegisterInstanceInDHTModule()));*/

    // Initialize Replicator.
    Replicator.setConfig(config_);
    Replicator.setAppKey(appKey_);
  }

  protected void startPeer() {
    networkThread_.start();
    dispatcherThread_.start();
  }

  protected void runBroker() {
    broker_.runThroughDispatcher();
  }

  protected void runAsyncModules() {
    // Periodically checking asynchronous messages starting from now.
    peerTimer_.scheduleRepeatedJob(injector_.getProvider(RetrieveAsynchronousMessagesModule.class),
        0L, RetrieveAsynchronousMessagesModule.EXECUTION_PERIOD);

    // Add found peer to synchro peers.
    MessageGenerator addFoundSynchroPeer = new MessageGenerator() {
      @Override
      public Message generate() {
        return new JobInitMessage(new AddSynchroPeerModule());
      }
    };
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
