package org.nebulostore.appcore;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.api.GetNebuloObjectModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;

/**
 * NebuloObject - object that is stored in replicas and identified by NabuloAddress
 * (currently NebuloFile, NebuloList or FileChunk).
 * @author bolek
 */
public abstract class NebuloObject implements Serializable {

  private static final long serialVersionUID = 7791201890856369839L;
  // TODO(bolek): Is this constant more global?
  protected static final int TIMEOUT_SEC = 60;
  protected static BlockingQueue<Message> dispatcherQueue_;

  // TODO(bolek): final?
  protected NebuloAddress address_;
  protected transient CommAddress sender_;

  public static void initObjectApi(BlockingQueue<Message> queue) {
    dispatcherQueue_ = queue;
  }

  public static NebuloObject fromAddress(NebuloAddress key) throws NebuloException {
    // Create a handler and run it through dispatcher.
    GetNebuloObjectModule module = new GetNebuloObjectModule(key, dispatcherQueue_);
    // Exception from getResult() is simply passed to the user.
    return module.getResult(TIMEOUT_SEC);
  }

  protected NebuloObject() { }

  public NebuloAddress getAddress() {
    return address_;
  }

  public void setSender(CommAddress sender) {
    sender_ = sender;
  }

  public void delete() {
    // TODO(bolek).
  }

  /**
   * Commits all operations - invoked by user.
   * @throws NebuloException
   */
  public void sync() throws NebuloException {
    // TODO(bolek): return type? exception? sync/async?
    runSync();
  }

  protected abstract void runSync() throws NebuloException;
}
