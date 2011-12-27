package org.nebulostore.replicator.messages;

import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author bolek
 * This is a request for replication of a particular object.
 */
public class StoreObjectMessage extends CommMessage {
  EncryptedEntity encryptedObject_;

  public StoreObjectMessage(CommAddress sourceAddress, CommAddress destAddress) {
    super(sourceAddress, destAddress);
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
