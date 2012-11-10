package org.nebulostore.async.messages;

import org.nebulostore.appcore.InstanceID;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * ACK message send by peer that succesfully received asynchronous messages.
 * @author szymonmatejczyk
 */
public class GotAsynchronousMessagesMessage extends CommMessage {
  InstanceID recipient_;

  public GotAsynchronousMessagesMessage(String jobId,
      CommAddress sourceAddress, CommAddress destAddress) {
    super(jobId, sourceAddress, destAddress);
  }

  private static final long serialVersionUID = 3450313559562082115L;

  public GotAsynchronousMessagesMessage(String jobId,
      CommAddress sourceAddress, CommAddress destAddress, InstanceID recipient) {
    super(jobId, sourceAddress, destAddress);
    recipient_ = recipient;
  }

  public InstanceID getRecipient() {
    return recipient_;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
