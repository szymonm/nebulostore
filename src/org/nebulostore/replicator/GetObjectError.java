package org.nebulostore.replicator;

import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author szymonmatejczyk
 */
public class GetObjectError extends NebuloException {
  final String message_;

  public String getMessage() {
    return message_;
  }

  public GetObjectError(String message) {
    super();
    message_ = message;
  }
}
