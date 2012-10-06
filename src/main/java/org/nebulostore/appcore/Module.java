package org.nebulostore.appcore;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Base class for all modules.
 * Module is an object that is runnable and communicates via in/out queues.
 */
public abstract class Module implements Runnable {

  protected BlockingQueue<Message> inQueue_;
  protected BlockingQueue<Message> outQueue_;
  // Is this thread ready to die (false by default). This is set by endModule() method.
  private boolean isFinished_;

  private static Logger logger_ = Logger.getLogger(Module.class);

  public Module() { }

  public Module(BlockingQueue<Message> inQueue, BlockingQueue<Message> outQueue) {
    outQueue_ = outQueue;
    inQueue_ = inQueue;
  }

  public BlockingQueue<Message> getInQueue() {
      return inQueue_;
  }

  public BlockingQueue<Message> getOutQueue() {
      return outQueue_;
  }

  public void setInQueue(BlockingQueue<Message> inQueue) {
    inQueue_ = inQueue;
  }

  public void setOutQueue(BlockingQueue<Message> outQueue) {
    outQueue_ = outQueue;
  }

  public void endModule() {
    isFinished_ = true;
  }

  @Override
  public void run() {
    while (!isFinished_) {
      try {
        processMessage(inQueue_.take());
        // If isFinished_ is set now, the thread will die.
      } catch (InterruptedException exception) {
        logger_.warn("Received InterruptedException from inQueue.");
        continue;
      } catch (NebuloException exception) {
        logger_.error("Received NebuloException from processMessage(): " + exception.getMessage());
        continue;
      }
    }
  }

  protected void processMessage(Message message) throws NebuloException { }

}
