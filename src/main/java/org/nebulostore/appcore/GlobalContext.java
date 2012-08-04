package org.nebulostore.appcore;

import java.util.concurrent.BlockingQueue;

import org.nebulostore.communication.CommunicationPeer;

/**
 * Data need by all Contexts.
 * @author szymonmatejczyk
 */
public final class GlobalContext {
  private static GlobalContext instance_;

  public InstanceID instanceID_;

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

  public InstanceID getInstanceID() {
    if (instanceID_ == null) {
      instanceID_ = new InstanceID(CommunicationPeer.getPeerAddress());
    }
    return instanceID_;
  }

  public void setInstanceID(InstanceID instanceID) {
    instanceID_ = instanceID;
  }
}
