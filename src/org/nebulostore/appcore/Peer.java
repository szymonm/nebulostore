package org.nebulostore.appcore;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.dispatcher.Dispatcher;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;

/**
 * @author marcin This is the entry point for a regular peer with full
 *         functionality.
 */
public final class Peer {
  private static Logger logger_ = Logger.getLogger(Peer.class);
  private static BlockingQueue<Message> networkInQueue_;
  private static BlockingQueue<Message> dispatcherInQueue_;

  private Peer() { }

  /**
   * @param args
   *          Command line arguments.
   */
  public static void main(String[] args) {
    runPeer();
  }

  public static void runPeer() {
    networkInQueue_ = new LinkedBlockingQueue<Message>();
    dispatcherInQueue_ = new LinkedBlockingQueue<Message>();
    ApiFacade.initApi(dispatcherInQueue_);
    NebuloObject.initObjectApi(dispatcherInQueue_);

    // Create dispatcher - outQueue will be passed to newly created tasks.
    Thread dispatcherThread = new Thread(new Dispatcher(dispatcherInQueue_, networkInQueue_));
    // Create network module.
    Thread networkThread = null;
    try {
      networkThread = new Thread(new CommunicationPeer(networkInQueue_, dispatcherInQueue_));
    } catch (NebuloException exception) {
      logger_.fatal("Error while creating CommunicationPeer");
      exception.printStackTrace();
      System.exit(-1);
    }
    networkThread.start();
    dispatcherThread.start();

    // Wait for threads to finish execution.
    try {
      // TODO: Make CommunicationPeer exit cleanly.
      //networkThread.join();
      dispatcherThread.join();
    } catch (InterruptedException exception) {
      logger_.fatal("Interrupted");
      return;
    }
  }

  public static void quitNebuloStore() {
    dispatcherInQueue_.add(new KillDispatcherMessage());
  }
}
