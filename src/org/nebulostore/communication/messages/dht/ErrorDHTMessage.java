package org.nebulostore.communication.messages.dht;

import org.nebulostore.communication.exceptions.CommException;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author marcin
 */
public class ErrorDHTMessage extends CommMessage {

  private final CommException exception_;

  public ErrorDHTMessage(CommException exception) {
    super(null, null);
    exception_ = exception;
  }

  public CommException getException() {
    return exception_;
  }
}
