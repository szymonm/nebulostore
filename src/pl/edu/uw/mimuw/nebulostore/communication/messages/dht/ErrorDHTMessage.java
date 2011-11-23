package pl.edu.uw.mimuw.nebulostore.communication.messages.dht;

import pl.edu.uw.mimuw.nebulostore.communication.exceptions.CommException;
import pl.edu.uw.mimuw.nebulostore.communication.messages.CommMessage;

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
