package org.nebulostore.async.messages;

import org.nebulostore.appcore.InstanceID;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.async.ResponseWithAsynchronousMessagesModule;
import org.nebulostore.broker.BrokerContext;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Message send, when peer wants to receive messages waiting for him.
 */
public class GetAsynchronousMessagesMessage extends CommMessage {
  private static final long serialVersionUID = 132756955341183967L;

  InstanceID recipient_;

  public GetAsynchronousMessagesMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, InstanceID recipient) {
    super(jobId, sourceAddress, destAddress);
    recipient_ = recipient;
  }

  public InstanceID getRecipient() {
    return recipient_;
  }

  @Override
  public JobModule getHandler() {
    return new ResponseWithAsynchronousMessagesModule(BrokerContext.getInstance());
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
