package org.nebulostore.api;

import java.util.concurrent.BlockingQueue;

import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.NebuloObject;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;

/**
 * @author bolek
 * Class that handles API calls via static methods.
 */
public final class ApiFacade {

  private static final int TIMEOUT_SEC = 60;
  private static BlockingQueue<Message> dispatcherQueue_;

  public static void initApi(BlockingQueue<Message> queue) {
    dispatcherQueue_ = queue;
  }

  public static NebuloObject getNebuloFile(NebuloAddress key) throws NebuloException {
    // Create a handler and run it through dispatcher.
    GetNebuloFileModule module = new GetNebuloFileModule(key, dispatcherQueue_);
    // Exception from getResult() is simply passed to the user.
    return module.getResult(TIMEOUT_SEC);
  }

  public static void putKey(AppKey appKey) throws NebuloException {
    // Create a handler and run it through dispatcher.
    PutKeyModule module = new PutKeyModule(appKey, dispatcherQueue_);
    // Exception from getResult() is simply passed to the user.
    module.getResult(TIMEOUT_SEC);
  }

  public static void quitNebuloStore() {
    dispatcherQueue_.add(new KillDispatcherMessage());
  }

  private ApiFacade() { }
}
