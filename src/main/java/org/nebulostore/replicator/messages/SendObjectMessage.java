package org.nebulostore.replicator.messages;

import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author bolek
 * This is a message containing requested object.
 */
public class SendObjectMessage extends CommMessage {
  public EncryptedEntity encryptedEntity_;

  public SendObjectMessage(CommAddress sourceAddress, CommAddress destAddress,
      EncryptedEntity encryptedObject) {
    super(sourceAddress, destAddress);
    encryptedEntity_ = encryptedObject;
  }

  public SendObjectMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, EncryptedEntity encryptedObject) {
    super(jobId, sourceAddress, destAddress);
    encryptedEntity_ = encryptedObject;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
