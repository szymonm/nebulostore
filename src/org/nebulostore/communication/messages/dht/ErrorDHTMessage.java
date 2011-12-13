package org.nebulostore.communication.messages.dht;

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
}
