package org.nebulostore.replicator.messages;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.replicator.core.TransactionAnswer;

/**
 * Sending transaction result to remote replicator.
 */
public class TransactionResultMessage extends ReplicatorMessage {
  private static final long serialVersionUID = 4182392038587696789L;

  private final TransactionAnswer result_;

  public TransactionResultMessage(String jobId, CommAddress destAddress, TransactionAnswer result) {
    super(jobId, destAddress);
    result_ = result;
  }

  public TransactionAnswer getResult() {
    return result_;
  }
}
