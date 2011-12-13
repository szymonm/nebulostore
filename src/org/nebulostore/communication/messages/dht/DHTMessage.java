package org.nebulostore.communication.messages.dht;

import java.io.Serializable;

import org.nebulostore.appcore.Message;

/**
 * @author Marcin Walas
 */
public abstract class DHTMessage extends Message implements Serializable {

  public DHTMessage(String id) {
    super(id);
  }

}
