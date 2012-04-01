package org.nebulostore.dispatcher.messages;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author bolek
 * This message causes dispatcher termination.
 */
public class KillDispatcherMessage extends Message {
  private static final long serialVersionUID = -6650450197366063482L;

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
