package org.nebulostore.appcore;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.dispatcher.Dispatcher;

/**
 * @author marcin
 * This is the entry point for a regular peer with full functionality.
 */
public final class Peer {
  private Peer() { }

  /**
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    BlockingQueue<Message> networkInQueue = new LinkedBlockingQueue<Message>();
    BlockingQueue<Message> dispatcherInQueue = new LinkedBlockingQueue<Message>();

    // Create dispatcher - outQueue will be passed to newly created tasks.
    Thread dispatcherThread = new Thread(new Dispatcher(dispatcherInQueue, networkInQueue));
    // Create network module.
    Thread networkThread = new Thread(new CommunicationPeer(networkInQueue, dispatcherInQueue));
    networkThread.start();
    dispatcherThread.start();

    // Wait for threads to finish execution.
    try {
      networkThread.join();
      dispatcherThread.join();
    } catch (InterruptedException e) {
    }
  }
}
