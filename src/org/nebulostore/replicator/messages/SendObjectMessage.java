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
  public EncryptedEntity encryptedObject_;

  public SendObjectMessage(CommAddress sourceAddress, CommAddress destAddress,
      EncryptedEntity encryptedObject) {
    super(sourceAddress, destAddress);
    encryptedObject_ = encryptedObject;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
