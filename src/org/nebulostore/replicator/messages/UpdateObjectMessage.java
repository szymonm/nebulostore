package org.nebulostore.replicator.messages;

import org.nebulostore.addressing.ObjectId;
import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.replicator.Replicator;

/**
 * This message is send to update object in Replicator's storage. After sending
 * this client expects ConfirmationMessage or ReplicatorErrorMessage.
 * @author szymonmatejczyk
 */
public class UpdateObjectMessage extends CommMessage {
  ObjectId objectId_;
  EncryptedEntity encryptedEntity_;

  public UpdateObjectMessage(String jobId, CommAddress sourceAddress,
      CommAddress destinationAddress, ObjectId objectId,
      EncryptedEntity encryptedEntity) {
    super(jobId, sourceAddress, destinationAddress);
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
