package pl.edu.uw.mimuw.nebulostore.appcore;

import java.util.concurrent.BlockingQueue;

/**
 * Base class for all modules.
 *
 */
public abstract class Module implements IModule, Runnable {

  protected BlockingQueue<Message> inQueue_;
  protected BlockingQueue<Message> outQueue_;

  public Module() {
  }

  public Module(BlockingQueue<Message> inQueue,
                BlockingQueue<Message> outQueue) {
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
      } catch (InterruptedException e) {
        break;
      }
    }
  }
}
