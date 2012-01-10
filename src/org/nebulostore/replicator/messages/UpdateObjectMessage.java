package org.nebulostore.replicator.messages;

import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.ObjectId;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

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

  public EncryptedEntity getEncryptedEntity() {
    return encryptedEntity_;
  }

  public ObjectId getObjectId() {
    return objectId_;
  }
}
