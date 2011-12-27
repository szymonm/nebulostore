package org.nebulostore.replicator.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.ObjectId;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author bolek
 * This is a request to delete a particular object from a peer that is replicating it.
 */
public class DeleteObjectMessage extends CommMessage {
  public ObjectId objectId_;

  public DeleteObjectMessage(CommAddress sourceAddress, CommAddress destAddress) {
    super(sourceAddress, destAddress);
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
