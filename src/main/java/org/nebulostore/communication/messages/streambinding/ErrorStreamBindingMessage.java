package org.nebulostore.communication.messages.streambinding;

import org.nebulostore.appcore.Message;

/**
 * @author Marcin Walas
 *
 */
public class ErrorStreamBindingMessage extends Message {

  private static final long serialVersionUID = 5064300465038959656L;

  private final StreamBindingMessage message_;
  private final Exception networkException_;

  /**
   * @param msg
   * @param error
   */
  public ErrorStreamBindingMessage(StreamBindingMessage msg, Exception error) {
    message_ = msg;
    networkException_ = error;
  }

  /**
   * @return
   */
  public StreamBindingMessage getMessage() {
    return message_;
  }

  /**
   * @return
   */
  public Exception getNetworkException() {
    return networkException_;
  }

}
