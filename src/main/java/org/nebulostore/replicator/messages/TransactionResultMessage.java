package org.nebulostore.replicator.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.replicator.TransactionAnswer;

/**
 * Sending transaction result to remote replicator.
 */
public class TransactionResultMessage extends CommMessage {
  private static final long serialVersionUID = 4182392038587696789L;

  private final TransactionAnswer result_;

  public TransactionResultMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, TransactionAnswer result) {
    super(jobId, sourceAddress, destAddress);
    result_ = result;
  }

  public TransactionAnswer getResult() {
    return result_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
