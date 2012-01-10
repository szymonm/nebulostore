package org.nebulostore.replicator.messages;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * This message is send to idicate that execution of message send to the
 * Replicator encountered an error. The error message is written in message_.
 * @author szymonmatejczyk
 */
public class ReplicatorErrorMessage extends CommMessage {
  String message_;

  public ReplicatorErrorMessage(String jobId, CommAddress sourceAddress,
      CommAddress destinationAddress, String message) {
    super(jobId, sourceAddress, destinationAddress);
    message_ = message;
  }

  public String getMessage() {
    return message_;
  }
}
