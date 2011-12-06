package org.nebulostore.appcore;

import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Base class for messages.
 */
public abstract class Message {

  public Message() {
    msgId_ = "unnamed_message";
  }

  public Message(String msgID) {
    msgId_ = msgID;
  }

  public void accept(MessageVisitor visitor) throws NebuloException {
    visitor.visit(this);
  }

  public JobModule getHandler() throws NebuloException {
    // TODO(bolek): Change it into a more specific exception type.
    throw new NebuloException("This is not an initializing message!");
  }

  public String getId() {
    return msgId_;
  }

  /*
   * To be used in merging queues mechanism
   */
  public String queueURI_;
  protected String msgId_;
}
