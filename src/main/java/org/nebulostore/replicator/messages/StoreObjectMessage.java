package org.nebulostore.replicator.messages;

import org.nebulostore.addressing.ObjectId;
import org.nebulostore.appcore.EncryptedObject;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.replicator.Replicator;

/**
 * @author bolek
 * This is a request for replication of a particular object.
 */
public class StoreObjectMessage extends CommMessage {
  ObjectId objectId_;
  EncryptedObject encryptedEntity_;
  private final String sourceJobId_;
  /*
  public StoreObjectMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress) {
    super(jobId, sourceAddress, destAddress);
  }
   */
  public StoreObjectMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress,
      ObjectId objectId, EncryptedObject encryptedEntity, String sourceJobId) {
    super(jobId, sourceAddress, destAddress);
    objectId_ = objectId;
    encryptedEntity_ = encryptedEntity;
    sourceJobId_ = sourceJobId;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public EncryptedObject getEncryptedEntity() {
    return encryptedEntity_;
  }

  public ObjectId getObjectId() {
    return objectId_;
  }

  public String getSourceJobId() {
    return sourceJobId_;
  }

  @Override
  public JobModule getHandler() {
    return new Replicator(jobId_, null, null);
  }


}
