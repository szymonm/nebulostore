package org.nebulostore.communication.messages.testing;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Message send to indicate test error.
 * @author szymonmatejczyk
 *
 */
public class ErrorTestMessage extends CommMessage {
  private static final long serialVersionUID = -38582973328370010L;

  private final String message_;

  public ErrorTestMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, String message) {
    super(jobId, sourceAddress, destAddress);
    message_ = message;
  }

  public String getMessage() {
    return message_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
