package org.nebulostore.api;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.ObjectId;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Successful reply from PutKey API call.
 */
public class ApiPutKeyMessage extends ApiMessage {
  public ApiPutKeyMessage(ObjectId objId) {
    objId_ = objId;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public ObjectId getObjectId() {
    return objId_;
  }

  protected ObjectId objId_;
}
