package org.nebulostore.communication.messages.broker.asynchronous;

import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.communication.address.CommAddress;

/**
 * Message send to peer when he needs to update file with @objectId,
 * that he stores, from @updateFrom.
 * @author szymonmatejczyk
 */
public class UpdateFileMessage extends AsynchronousMessage {
  NebuloAddress objectId_;

  /* It is assumed that instance of Nebulostore has persistent CommAddress. */
  CommAddress updateFrom_;

  public UpdateFileMessage(NebuloAddress objectId, CommAddress updateFrom) {
    super();
    objectId_ = objectId;
    updateFrom_ = updateFrom;
  }

  public NebuloAddress getObjectId() {
    return objectId_;
  }

  public CommAddress getUpdateFrom() {
    return updateFrom_;
  }
}
