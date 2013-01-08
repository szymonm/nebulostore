package org.nebulostore.communication.exceptions;

import java.io.Serializable;


/**
 * Indicates lack of given address resolver's "pool" of addresses.
 *
 * @author Grzegorz Milka
 */
public class AddressNotPresentException extends Exception implements Serializable {
  private static final long serialVersionUID = 8868169624650242139L;

  public AddressNotPresentException() {
  }

  public AddressNotPresentException(String msg) {
    super(msg);
  }

  public AddressNotPresentException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public AddressNotPresentException(Throwable cause) {
    super(cause);
  }
}
