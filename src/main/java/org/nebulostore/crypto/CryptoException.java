package org.nebulostore.crypto;

import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Exceptions thrown from CryptoUtils class.
 */
public class CryptoException extends NebuloException {
  public CryptoException(String message) {
    super(message);
  }
}
