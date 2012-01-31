package org.nebulostore.appcore.exceptions;

/**
 * @author bolek
 * Base class for NebuloStore exceptions.
 */
public class NebuloException extends Exception {

  protected final Exception innerException_;

  public NebuloException() {
    innerException_ = null;
  }

  public NebuloException(String message) {
    super(message);
    innerException_ = null;
  }

  public NebuloException(Exception innerException) {
    innerException_ = innerException;
  }

  public NebuloException(String message, Exception innerException) {
    super(message);
    innerException_ = innerException;
  }

  public Exception getInnerException() {
    return innerException_;
  }
}
