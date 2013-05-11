package org.nebulostore.async.messages;

import org.nebulostore.appcore.addressing.NebuloAddress;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.appcore.model.NebuloObject;

/**
 * Message indicating that file with objectId_ should be updated. New object carried in the message.
 * @author sm262956
 */
public class UpdateSmallNebuloObjectMessage extends AsynchronousMessage {
  private static final long serialVersionUID = -1099497850800611597L;

  NebuloAddress objectId_;
  NebuloObject object_;

  public UpdateSmallNebuloObjectMessage(NebuloAddress objectId, NebuloObject object) {
    objectId_ = objectId;
    object_ = object;
  }

  public NebuloAddress getObjectId() {
    return objectId_;
  }

  public NebuloObject getFile() {
    return object_;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
