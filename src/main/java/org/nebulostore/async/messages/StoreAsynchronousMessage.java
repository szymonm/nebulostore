package org.nebulostore.async.messages;

import org.nebulostore.appcore.InstanceID;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.async.StoreAsynchronousMessagesModule;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Store asynchronous message.
 * @author szymonmatejczyk
 */
public class StoreAsynchronousMessage extends CommMessage {
  InstanceID recipient_;
  AsynchronousMessage message_;

  public StoreAsynchronousMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, InstanceID recipient,
      AsynchronousMessage message) {
    super(jobId, sourceAddress, destAddress);
    recipient_ = recipient;
    message_ = message;
  }

  public AsynchronousMessage getMessage() {
    return message_;
  }

  public InstanceID getRecipient() {
    return recipient_;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  @Override
  public JobModule getHandler() throws NebuloException {
    return new StoreAsynchronousMessagesModule();
  }
}
