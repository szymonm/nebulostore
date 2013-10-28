package org.nebulostore.utils;

/**
 * An exception that provides an easy and safe way to add contextual information.
 *
 * @author Grzegorz Milka
 *
 */
public class ContextedException extends Exception {
  private static final long serialVersionUID = 1L;
  private final Object object_;
  public ContextedException(Exception cause, Object object) {
    super(cause);
    object_ = object;
  }

  public ContextedException(String message, Exception cause, Object object) {
    super(message, cause);
    object_ = object;
  }

  public Object getContext() {
    return object_;
  }

  public String toString() {
    return String.format("ContextedException[context: %s, message: %s, cause: %s]",
        object_, getMessage(), getCause());
  }
}
