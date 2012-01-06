package org.nebulostore.appcore;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.nebulostore.api.ApiFacade;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.dispatcher.Dispatcher;

/**
 * @author marcin This is the entry point for a regular peer with full
 *         functionality.
 */
public final class Peer {
  private Peer() {
  }

  /**
   * @param args
   *          Command line arguments.
   */
  public static void main(String[] args) {
    BlockingQueue<Message> networkInQueue = new LinkedBlockingQueue<Message>();
    BlockingQueue<Message> dispatcherInQueue = new LinkedBlockingQueue<Message>();
    ApiFacade.setDispatcherQueue(dispatcherInQueue);

    // Create dispatcher - outQueue will be passed to newly created tasks.
    Thread dispatcherThread = new Thread(new Dispatcher(dispatcherInQueue,
        networkInQueue));
    // Create network module.
    Thread networkThread = null;
    try {
      networkThread = new Thread(new CommunicationPeer(networkInQueue,
          dispatcherInQueue));
    } catch (NebuloException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      System.exit(-1);
    }
    networkThread.start();
    dispatcherThread.start();

    // Wait for threads to finish execution.
    try {
      networkThread.join();
      dispatcherThread.join();
    } catch (InterruptedException e) {
      // TODO(bolek): Log it?
      return;
    }
  }
}
