package org.nebulostore.communication.exceptions;

import java.io.Serializable;

import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Is being thrown by communication package classes whether some serious error
 * occurs while trying to contact remote machine.
 *
 * @author marcin
 */
public class CommException extends NebuloException implements Serializable {

  public CommException() {
  }

  public CommException(Exception e) {
    super(e);
  }

}
