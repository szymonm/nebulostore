package org.nebulostore.appcore.exceptions;

/**
 * @author bolek
 * Base class for NebuloStore exceptions.
 */
public class NebuloException extends Exception {
  public NebuloException() { }
  public NebuloException(String message) {
    super(message);
  }
}
