package org.nebulostore.api;

import java.util.concurrent.BlockingQueue;

import org.nebulostore.addressing.AppKey;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author bolek
 * Class that handles API calls via static methods.
 */
public final class ApiFacade {
  // TODO(bolek): This class is going to be removed!

  private static final int TIMEOUT_SEC = 60;
  private static BlockingQueue<Message> dispatcherQueue_;

  public static void initApi(BlockingQueue<Message> queue) {
    dispatcherQueue_ = queue;
  }

  public static void putKey(AppKey appKey) throws NebuloException {
    // Create a handler and run it through dispatcher.
    PutKeyModule module = new PutKeyModule(appKey, dispatcherQueue_);
    // Exception from getResult() is simply passed to the user.
    module.getResult(TIMEOUT_SEC);
  }

  private ApiFacade() { }
}
