package org.nebulostore.appcore;

import java.util.concurrent.BlockingQueue;

import org.nebulostore.appcore.exceptions.KillModuleException;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Base class for all modules.
 * Module is an object that is runnable and communicates via in/out queues.
 */
public abstract class Module implements Runnable {

  protected BlockingQueue<Message> inQueue_;
  protected BlockingQueue<Message> outQueue_;

  public Module() {
  }

  public Module(BlockingQueue<Message> inQueue, BlockingQueue<Message> outQueue) {
    outQueue_ = outQueue;
    inQueue_ = inQueue;
  }

  public void setInQueue(BlockingQueue<Message> inQueue) {
    inQueue_ = inQueue;
  }

  public void setOutQueue(BlockingQueue<Message> outQueue) {
    outQueue_ = outQueue;
  }

  @Override
  public void run() {
    while (true) {
      try {
        processMessage(inQueue_.take());
      } catch (InterruptedException exception) {
        // TODO: Log interrupt?
      } catch (KillModuleException exception) {
        break;
      } catch (NebuloException exception) {
      }
    }
  }

  protected void processMessage(Message message) throws NebuloException { }
}
