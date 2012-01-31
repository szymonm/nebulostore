package org.nebulostore.replicator.messages;

import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.ObjectId;
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
  EncryptedEntity encryptedEntity_;

  public StoreObjectMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress) {
    super(jobId, sourceAddress, destAddress);
  }

  public StoreObjectMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress,
      ObjectId objectId, EncryptedEntity encryptedEntity) {
    this(jobId, sourceAddress, destAddress);
    objectId_ = objectId;
    encryptedEntity_ = encryptedEntity;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public EncryptedEntity getEncryptedEntity() {
    return encryptedEntity_;
  }

  public ObjectId getObjectId() {
    return objectId_;
  }

  @Override
  public JobModule getHandler() {
    return new Replicator(null, null);
  }

}
