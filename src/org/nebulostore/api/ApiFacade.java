package org.nebulostore.api;

import java.util.concurrent.BlockingQueue;

import org.nebulostore.appcore.AppKey;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.NebuloKey;
import org.nebulostore.appcore.ObjectId;
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

  public static NebuloFile getNebuloFile(NebuloKey key) throws NebuloException {
    // Create a handler and run it through dispatcher.
    GetNebuloFileModule module = new GetNebuloFileModule(key, dispatcherQueue_);
    // Exception from getResult() is simply passed to the user.
    return module.getResult(TIMEOUT_SEC);
  }

  public static NebuloKey putKey(AppKey appKey) throws NebuloException {
    // Create a handler and run it through dispatcher.
    PutKeyModule module = new PutKeyModule(appKey, dispatcherQueue_);
    // Exception from getResult() is simply passed to the user.
    ObjectId dirId = module.getResult(TIMEOUT_SEC);
    NebuloKey retKey = new NebuloKey();
    retKey.appKey_ = appKey;
    retKey.objectId_ = dirId;
    return retKey;
  }

  public static void quitNebuloStore() {
    dispatcherQueue_.add(new KillDispatcherMessage());
  }

  private ApiFacade() { }
}
