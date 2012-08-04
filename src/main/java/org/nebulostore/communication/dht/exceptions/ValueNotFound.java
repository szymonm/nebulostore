package org.nebulostore.communication.dht.exceptions;

import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * DHT value not found.
 */
public class ValueNotFound extends NebuloException {


  public ValueNotFound(String msg) {
    super(msg);
  }

}
