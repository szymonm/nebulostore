package org.nebulostore.communication.messages.dht;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.exceptions.CommException;

/**
 * @author marcin
 */
public class ErrorDHTMessage extends OutDHTMessage {

  /**
   */
  private final CommException exception_;

  public ErrorDHTMessage(InDHTMessage reqMessage, CommException exception) {
    super(reqMessage);
    exception_ = exception;
  }

  public CommException getException() {
    return exception_;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
