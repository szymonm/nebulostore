package org.nebulostore.communication.messages.dht;

import java.io.Serializable;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author Marcin Walas
 */
public abstract class DHTMessage extends Message implements Serializable {

  public DHTMessage(String id) {
    super(id);
  }

  @Override
  public abstract <R> R accept(MessageVisitor<R> visitor) throws NebuloException;
}
