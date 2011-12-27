package org.nebulostore.communication.messages.dht;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author marcin
 */
public class OkDHTMessage extends OutDHTMessage {

  public OkDHTMessage(InDHTMessage reqMessage) {
    super(reqMessage);
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
