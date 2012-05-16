package org.nebulostore.api;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.ReplicationGroup;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;

/**
 * @author bolek
 * Class that handles API calls via static methods.
 */
public final class ApiFacade {
  // TODO(bolek): This class is going to be removed!

  private static final int TIMEOUT_SEC = 60;
  private static BlockingQueue<Message> dispatcherQueue_;
  private static AppKey appKey_;

  public static void initApi(BlockingQueue<Message> queue) {
    dispatcherQueue_ = queue;
  }

  public static void putKey(AppKey appKey) throws NebuloException {
    appKey_ = appKey;
    // Create a handler and run it through dispatcher.
    PutKeyModule module = new PutKeyModule(appKey,
        new ReplicationGroup(new CommAddress[]{CommunicationPeer.getPeerAddress()},
            new BigInteger("0"), new BigInteger("1000000")), dispatcherQueue_);
    // Exception from getResult() is simply passed to the user.
    module.getResult(TIMEOUT_SEC);
  }

  private ApiFacade() { }

  public static AppKey getAppKey() {
    return appKey_;
  }
}
