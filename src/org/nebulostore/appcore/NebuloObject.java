package org.nebulostore.appcore;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.api.GetNebuloFileModule;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author bolek
 * Abstract base class for NebuloFile and NebuloList.
 */
public abstract class NebuloObject implements Serializable {

  private static final long serialVersionUID = 7791201890856369839L;
  // TODO(bolek): Is this constant more global?
  private static final int TIMEOUT_SEC = 60;
  private static BlockingQueue<Message> dispatcherQueue_;

  protected NebuloAddress address_;

  protected NebuloObject() { }

  public NebuloAddress getAddress() {
    return address_;
  }

  public static void initObjectApi(BlockingQueue<Message> queue) {
    dispatcherQueue_ = queue;
  }

  public static NebuloObject fromAddress(NebuloAddress key) throws NebuloException {
    // Create a handler and run it through dispatcher.
    GetNebuloFileModule module = new GetNebuloFileModule(key, dispatcherQueue_);
    // Exception from getResult() is simply passed to the user.
    return module.getResult(TIMEOUT_SEC);
  }
}
