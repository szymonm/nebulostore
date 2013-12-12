package org.nebulostore.peers;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

import org.apache.log4j.Logger;
import org.nebulostore.api.PutKeyModule;
import org.nebulostore.appcore.addressing.AppKey;
import org.nebulostore.appcore.addressing.ReplicationGroup;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.modules.EndModuleMessage;
import org.nebulostore.async.AddSynchroPeerModule;
import org.nebulostore.async.RetrieveAsynchronousMessagesModule;
import org.nebulostore.broker.Broker;
import org.nebulostore.communication.CommunicationPeerFactory;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.dispatcher.Dispatcher;
import org.nebulostore.dispatcher.JobInitMessage;
import org.nebulostore.networkmonitor.NetworkContext;
import org.nebulostore.timer.MessageGenerator;
import org.nebulostore.timer.Timer;

/**
 * This is a regular peer with full functionality. It creates, connects and runs all modules.
 * To create a different peer, subclass Peer and set its class name in configuration.
 *
 * To customize the Peer, please override initializeModules(), runActively() and cleanModules().
 *
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
  protected Injector injector_;
  protected CommAddress commAddress_;
  protected Timer peerTimer_;

  private CommunicationPeerFactory commPeerFactory_;

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

    // Create core threads.
    Runnable dispatcher = new Dispatcher(dispatcherInQueue_, networkInQueue_, injector_);
    dispatcherThread_ = new Thread(dispatcher, "Dispatcher");
    Runnable commPeer = commPeerFactory_.newCommunicationPeer(networkInQueue_, dispatcherInQueue_);
    networkThread_ = new Thread(commPeer, "CommunicationPeer");
  }

  public void quitNebuloStore() {
    if (networkInQueue_ != null) {
      networkInQueue_.add(new EndModuleMessage());
    }
    if (dispatcherInQueue_ != null) {
      dispatcherInQueue_.add(new EndModuleMessage());
    }
  }

  @Override
  public final void run() {
    runPeer();
  }

  private void runPeer() {
    initializeModules();
    startCoreThreads();
    runActively();
    joinCoreThreads();
    cleanModules();
  }

  /**
   * Initialize all optional modules and schedule them for execution by dispatcher.
   * Override this method to run modules selectively.
   */
  protected void initializeModules() {
    runNetworkMonitor();
    runBroker();
    runAsyncMessaging();
  }

  /**
   * Logic to be executed when the application is already running.
   * Override this method when operations on active application are necessary.
   */
  protected void runActively() {
    // TODO: Move putkey to separate module or at least make it non-blocking.
    putKey(appKey_);
  }

  /**
   * Actions performed on exit.
   * Override this method when special clean-up is required.
   */
  protected void cleanModules() {
    // Empty by default.
  }

  protected void runNetworkMonitor() {
    NetworkContext.getInstance().setCommAddress(commAddress_);
    NetworkContext.getInstance().setDispatcherQueue(dispatcherInQueue_);
  }

  protected void runBroker() {
    broker_.runThroughDispatcher();
  }

  protected void runAsyncMessaging() {
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
  }

  protected void putKey(AppKey appKey) {
    // TODO(bolek): This should be part of broker.
    PutKeyModule module = new PutKeyModule(new ReplicationGroup(new CommAddress[]{commAddress_},
        BigInteger.ZERO, new BigInteger("1000000")), dispatcherInQueue_);
    try {
      module.getResult(30);
    } catch (NebuloException exception) {
      logger_.error("Unable to execute PutKeyModule!", exception);
    }
  }

  protected void startCoreThreads() {
    networkThread_.start();
    dispatcherThread_.start();
  }

  protected void joinCoreThreads() {
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
