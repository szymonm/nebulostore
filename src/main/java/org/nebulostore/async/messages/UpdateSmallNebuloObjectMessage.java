package org.nebulostore.async.messages;

import org.nebulostore.appcore.model.NebuloObject;

/**
 * Message indicating that file with objectId_ should be updated. New object carried in the message.
 * @author sm262956
 */
public class UpdateSmallNebuloObjectMessage extends AsynchronousMessage {
  private static final long serialVersionUID = -1099497850800611597L;

  NebuloObject object_;

  public UpdateSmallNebuloObjectMessage(NebuloObject object) {
    object_ = object;
  }

  public NebuloObject getFile() {
    return object_;
  }
}
