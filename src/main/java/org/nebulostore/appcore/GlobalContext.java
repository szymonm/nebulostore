package org.nebulostore.appcore;

import java.util.concurrent.BlockingQueue;

/**
 * Data need by all Contexts.
 * @author szymonmatejczyk
 */
public final class GlobalContext {
  private static GlobalContext instance_;
  public static GlobalContext getInstance() {
    if (instance_ == null)
      instance_ = new GlobalContext();
    return instance_;
  }

  private GlobalContext() {
  }

  BlockingQueue<Message> dispatcherQueue_;

  public BlockingQueue<Message> getDispatcherQueue() {
    return dispatcherQueue_;
  }

  public void setDispatcherQueue(BlockingQueue<Message> dispatcherQueue) {
    dispatcherQueue_ = dispatcherQueue;
  }
}
