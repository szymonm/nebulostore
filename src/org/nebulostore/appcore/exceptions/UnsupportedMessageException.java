package org.nebulostore.appcore.exceptions;

/**
 * @author bolek
 * Exception thrown from visitor after receiving message of unsupported type.
 */
public class UnsupportedMessageException extends NebuloException {
  public UnsupportedMessageException(String message) {
    super(message);
  }
}
