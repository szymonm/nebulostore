package org.nebulostore.replicator.messages;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * This message is send to idicate that execution of message send to the
 * Replicator encountered an error. The error message is written in message_.
 * @author szymonmatejczyk
 */
public class ReplicatorErrorMessage extends CommMessage {
  private static final long serialVersionUID = -686759042653122970L;

  String message_;

  public ReplicatorErrorMessage(String jobId, CommAddress sourceAddress,
      CommAddress destinationAddress, String message) {
    super(jobId, sourceAddress, destinationAddress);
    message_ = message;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public String getMessage() {
    return message_;
  }
}
