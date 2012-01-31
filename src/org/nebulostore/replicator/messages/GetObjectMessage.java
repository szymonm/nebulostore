package org.nebulostore.replicator.messages;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.ObjectId;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.replicator.Replicator;

/**
 * @author bolek
 * This is a request for a particular object sent to peer that hold this object's replica.
 * Sender waits for a response wrapped into SendObjectMessage.
 */
public class GetObjectMessage extends CommMessage {
  public ObjectId objectId_;

  public GetObjectMessage(CommAddress sourceAddress, CommAddress destAddress, ObjectId objectId) {
    super(sourceAddress, destAddress);
    objectId_ = objectId;
  }

  public GetObjectMessage(String jobId, CommAddress sourceAddress, CommAddress destAddress,
      ObjectId objectId) {
    super(jobId, sourceAddress, destAddress);
    objectId_ = objectId;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  @Override
  public JobModule getHandler() {
    return new Replicator(null, null);
  }

}
