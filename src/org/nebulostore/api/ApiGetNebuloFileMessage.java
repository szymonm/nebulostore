package org.nebulostore.api;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Successful reply from GetNebuloFile API call.
 */
public class ApiGetNebuloFileMessage extends ApiMessage {
  public ApiGetNebuloFileMessage(NebuloFile file) {
    file_ = file;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public NebuloFile getNebuloFile() {
    return file_;
  }

  protected NebuloFile file_;
}
