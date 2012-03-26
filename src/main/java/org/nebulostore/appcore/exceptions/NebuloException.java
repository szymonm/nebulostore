package org.nebulostore.appcore.exceptions;

/**
 * @author bolek
 * Base class for NebuloStore exceptions.
 */
public class NebuloException extends Exception {

  protected final Exception cause_;

  public NebuloException() {
    cause_ = null;
  }

  public NebuloException(String message) {
    super(message);
    cause_ = null;
  }

  public NebuloException(Exception cause) {
    cause_ = cause;
  }

  public NebuloException(String message, Exception cause) {
    super(message);
    cause_ = cause;
  }

  public Exception getInnerException() {
    return cause_;
  }
}
