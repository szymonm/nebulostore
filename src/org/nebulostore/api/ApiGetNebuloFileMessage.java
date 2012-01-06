package org.nebulostore.api;

import org.nebulostore.appcore.NebuloFile;

/**
 * Successful reply from GetNebuloFile API call.
 */
public class ApiGetNebuloFileMessage extends ApiMessage {
  public ApiGetNebuloFileMessage(NebuloFile file) {
    file_ = file;
  }

  public NebuloFile getNebuloFile() {
    return file_;
  }

  protected NebuloFile file_;
}
