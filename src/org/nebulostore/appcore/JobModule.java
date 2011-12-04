package org.nebulostore.appcore;

import java.util.concurrent.BlockingQueue;

/**
 * @author bolek
 * Base class for all job handlers.
 */
public class JobModule extends Module {
  protected BlockingQueue<Message> networkQueue_;

  public void setNetworkQueue(BlockingQueue<Message> networkQueue) {
    networkQueue_ = networkQueue;
  }
}
