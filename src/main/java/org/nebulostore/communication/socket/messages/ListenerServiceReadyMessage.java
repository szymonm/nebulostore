package org.nebulostore.communication.socket.messages;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.messaging.MessageVisitor;

/**
 * Message sent by ListenerService when it is ready to accept messages.
 *
 * @author Grzegorz Milka
 */
public class ListenerServiceReadyMessage extends Message {
  private static final long serialVersionUID = -3142642028671602656L;

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
