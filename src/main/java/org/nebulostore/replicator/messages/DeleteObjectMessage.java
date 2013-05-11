package org.nebulostore.replicator.messages;

import org.nebulostore.appcore.addressing.ObjectId;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.appcore.modules.JobModule;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.replicator.Replicator;

/**
 * @author bolek
 * This is a request to delete a particular object from a peer that is replicating it.
 */
public class DeleteObjectMessage extends CommMessage {
  private static final long serialVersionUID = -587693375265935213L;

  public ObjectId objectId_;
  private final String sourceJobId_;

  public DeleteObjectMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, ObjectId objectId, String sourceJobId) {
    super(jobId, sourceAddress, destAddress);
    sourceJobId_ = sourceJobId;
    objectId_ = objectId;
  }

  public ObjectId getObjectId() {
    return objectId_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public String getSourceJobId() {
    return sourceJobId_;
  }

  @Override
  public JobModule getHandler() {
    return new Replicator(jobId_, null, null);
  }

}
