package org.nebulostore.communication.messages.dht;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author marcin
 */
public abstract class OutDHTMessage extends DHTMessage {
  private final InDHTMessage requestMessage_;

  public OutDHTMessage(InDHTMessage reqMessage) {
    super(reqMessage.getId());
    requestMessage_ = reqMessage;
  }

  public InDHTMessage getRequestMessage() {
    return requestMessage_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}