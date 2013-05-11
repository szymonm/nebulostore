package org.nebulostore.replicator.messages;

import org.nebulostore.appcore.addressing.NebuloAddress;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.appcore.modules.JobModule;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.replicator.Replicator;

/**
 * Message send to replicator by owner of an object, when he stores outdated version of object.
 *
 * Replicator should download the object again from another replica.
 */
public class ObjectOutdatedMessage extends CommMessage {
  private static final long serialVersionUID = -3210318617364486510L;

  private final NebuloAddress address_;

  public ObjectOutdatedMessage(CommAddress sourceAddress, CommAddress destAddress,
      NebuloAddress address) {
    super(sourceAddress, destAddress);
    address_ = address;
  }

  public NebuloAddress getAddress() {
    return address_;
  }

  @Override
  public JobModule getHandler() throws NebuloException {
    return new Replicator(jobId_, null, null);
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
