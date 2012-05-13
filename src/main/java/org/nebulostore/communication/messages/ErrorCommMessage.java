package org.nebulostore.communication.messages;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Message returned by the communication layer to Dispatcher if for some reason
 * the message failed to be sent. Receiver may assume that the destination host
 * is not in the network or there are massive latencies.
 *
 * @author Marcin Walas
 */
public class ErrorCommMessage extends Message {

  private static final long serialVersionUID = 5064300465038959656L;

  private final CommMessage message_;
  private final Exception networkException_;

  /**
   * @param msg message failed to send
   * @param error error that was a reason
   */
  public ErrorCommMessage(CommMessage msg, Exception error) {
    super(msg.getId());
    message_ = msg;
    networkException_ = error;
  }

  public CommMessage getMessage() {
    return message_;
  }

  public Exception getNetworkException() {
    return networkException_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
