package org.nebulostore.appcore.messaging;

import java.io.Serializable;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.modules.JobModule;
import org.nebulostore.crypto.CryptoUtils;

/**
 * Base class for messages.
 */
public abstract class Message implements Serializable {
  private static final long serialVersionUID = -2032656006415029507L;

  // ID used by Dispatcher to forward the message to proper thread (= running JobModule).
  protected final String jobId_;

  public Message() {
    jobId_ = CryptoUtils.getRandomString();
  }

  public Message(String jobID) {
    jobId_ = jobID;
  }

  public String getId() {
    return jobId_;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Message message = (Message) o;

    if (jobId_ != null ? !jobId_.equals(message.jobId_) : message.jobId_ != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return jobId_ != null ? jobId_.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Message{" +
        "jobId_='" + jobId_ + '\'' +
        "} " + super.toString();
  }

  /**
   * Accept method required by the visitor pattern.
   */
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public JobModule getHandler() throws NebuloException {
    // TODO(bolek): Change it into a more specific exception type.
    throw new NebuloException(getClass().getSimpleName() + " is not an initializing message type.");
  }
}
