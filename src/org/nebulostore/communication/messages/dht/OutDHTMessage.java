package org.nebulostore.communication.messages.dht;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author marcin
 */
public abstract class OutDHTMessage extends DHTMessage {
  public OutDHTMessage(InDHTMessage reqMessage) {
    super(reqMessage.getId());
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
