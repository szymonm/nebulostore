package org.nebulostore.communication.messages.dht;

import java.io.Serializable;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author marcin
 */
public abstract class DHTMessage extends Message implements Serializable {
  private static final long serialVersionUID = 2403620888224729984L;

  public DHTMessage(String id) {
    super(id);
  }

  @Override
  public abstract <R> R accept(MessageVisitor<R> visitor) throws NebuloException;
}
