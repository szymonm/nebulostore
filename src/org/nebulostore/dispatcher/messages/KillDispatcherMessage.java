package org.nebulostore.dispatcher.messages;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author bolek
 * This message causes dispatcher termination.
 */
public class KillDispatcherMessage extends Message {
  public void accept(MessageVisitor visitor) throws NebuloException {
    visitor.visit(this);
  }
}
