package org.nebulostore.communication.messages.dht;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author Marcin Walas
 */
public abstract class InDHTMessage extends DHTMessage {

  public InDHTMessage(String id) {
    super(id);
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
