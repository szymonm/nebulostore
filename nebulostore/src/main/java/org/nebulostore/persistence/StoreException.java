package org.nebulostore.persistence;

import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author Bolek Kulbabinski
 */
public class StoreException extends NebuloException {
  private static final long serialVersionUID = -7247035681103406736L;

  public StoreException(String message, Throwable cause) {
    super(message, cause);
  }

  public StoreException(String message) {
    super(message);
  }
}
