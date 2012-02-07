package org.nebulostore.appcore;

import java.util.concurrent.BlockingQueue;

import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;

/**
 * @author bolek
 * Base class for all job handlers - modules that are managed by dispatcher.
 * Message queues outQueue_ and inQueue_ are always connected to dispatcher.
 */
public abstract class JobModule extends Module {
  protected BlockingQueue<Message> networkQueue_;
  protected String jobId_;

  public void setNetworkQueue(BlockingQueue<Message> networkQueue) {
    networkQueue_ = networkQueue;
  }

  /*
   * Run this module through a JobInitMessage (with new random ID) sent to Dispatcher.
   */
  protected void runThroughDispatcher(BlockingQueue<Message> dispatcherQueue) {
    jobId_ = CryptoUtils.getRandomName();
    dispatcherQueue.add(new JobInitMessage(jobId_, this));
  }

  protected void endJobModule() {
    // Inform dispatcher that we are going to die.
    outQueue_.add(new JobEndedMessage(jobId_));
    // Inform run() (in base class) that this thread is ready to die.
    endModule();
  }
}
