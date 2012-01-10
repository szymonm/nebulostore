package org.nebulostore.replicator;

import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author szymonmatejczyk
 */
public class DeleteObjectException extends NebuloException {
  public DeleteObjectException(String message) {
    super(message);
  }

}
