package org.nebulostore.appcore;

import java.io.Serializable;

import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Base class for messages.
 */
public abstract class Message implements Serializable {
  private static final long serialVersionUID = -2032656006415029507L;

  public Message() {
    jobId_ = "unnamed_message";
  }

  public Message(String jobID) {
    jobId_ = jobID;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public JobModule getHandler() throws NebuloException {
    // TODO(bolek): Change it into a more specific exception type.
    throw new NebuloException("This is not an initializing message!");
  }

  public String getId() {
    return jobId_;
  }

  // ID used by Dispatcher to forward the message to proper thread (= running JobModule).
  protected String jobId_;
}
