package org.nebulostore.appcore;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.broker.Broker;
import org.nebulostore.broker.NetworkContext;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.jxta.JXTAPeer;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.Dispatcher;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;

/**
 * @author marcin This is the entry point for a regular peer with full
 *         functionality.
 */
public class Peer {
  private static Logger logger_ = Logger.getLogger(Peer.class);
  protected static BlockingQueue<Message> networkInQueue_;
  protected static BlockingQueue<Message> dispatcherInQueue_;
  private static Thread dispatcherThread_;
  private static Thread networkThread_;

  protected Peer() { }

  /**
   * @param args
   *          Command line arguments.
   */
  public static void main(String[] args) {

    DOMConfigurator.configure("resources/conf/log4j.xml");

    BigInteger appKey = BigInteger.ZERO;
    if (args.length < 1) {
      // Random AppKey if not provided.
      appKey = CryptoUtils.getRandomId();
    } else {
      appKey = new BigInteger(args[0]);
    }
    runPeer(new AppKey(appKey));
  }

  public static void runPeer(AppKey appKey) {
    startPeer(appKey);
    finishPeer();
  }

  protected static void startPeer(AppKey appKey) {
    networkInQueue_ = new LinkedBlockingQueue<Message>();
    dispatcherInQueue_ = new LinkedBlockingQueue<Message>();
    ApiFacade.initApi(dispatcherInQueue_);
    NebuloObject.initObjectApi(dispatcherInQueue_);

    // Create dispatcher - outQueue will be passed to newly created tasks.
    dispatcherThread_ = new Thread(new Dispatcher(dispatcherInQueue_, networkInQueue_));
    // Create network module.
    try {
      networkThread_ = new Thread(new CommunicationPeer(networkInQueue_, dispatcherInQueue_));
    } catch (NebuloException exception) {
      logger_.fatal("Error while creating CommunicationPeer");
      exception.printStackTrace();
      System.exit(-1);
    }
    // Create Broker.
    String brokerJobId = CryptoUtils.getRandomId().toString();
    dispatcherInQueue_.add(new JobInitMessage(brokerJobId, new Broker(brokerJobId, true)));
    NetworkContext.getInstance().setAppKey(appKey);
    GlobalContext.getInstance().setDispatcherQueue(dispatcherInQueue_);

    // Run everything.
    networkThread_.start();
    // Give network module some time to initialize.
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    dispatcherThread_.start();

    JXTAPeer.startFeeding_ = true;
  }

  protected static void finishPeer() {
    // Wait for threads to finish execution.
    try {
      // TODO: Make CommunicationPeer exit cleanly.
      //networkThread_.join();
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
