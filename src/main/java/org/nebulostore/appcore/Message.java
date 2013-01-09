package org.nebulostore.appcore;

import java.io.Serializable;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.crypto.CryptoUtils;

/**
 * Base class for messages.
 */
public abstract class Message implements Serializable {
  private static final long serialVersionUID = -2032656006415029507L;

  // ID used by Dispatcher to forward the message to proper thread (= running JobModule).
  protected String jobId_;

  public Message() {
    jobId_ = CryptoUtils.getRandomString();
  }

  protected Message(String jobID) {
    jobId_ = jobID;
  }

  /**
   * Accept method required by the visitor pattern. Use the following body in implementations.
   *     { return visitor.visit(this); }
   */
  public abstract <R> R accept(MessageVisitor<R> visitor) throws NebuloException;

  public JobModule getHandler() throws NebuloException {
    // TODO(bolek): Change it into a more specific exception type.
    throw new NebuloException("This is not an initializing message: " + this.getClass().toString());
  }

  public String getId() {
    return jobId_;
  }
}
