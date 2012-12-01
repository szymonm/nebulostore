package org.nebulostore.conductor.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Message class used to send test-specific messages.
 * @author bolek
 */
public abstract class UserCommMessage extends CommMessage {
  private static final long serialVersionUID = -2865092973190890821L;

  public UserCommMessage(CommAddress sourceAddress, CommAddress destAddress) {
    super(sourceAddress, destAddress);
  }

  public UserCommMessage(String jobId, CommAddress sourceAddress, CommAddress destAddress) {
    super(jobId, sourceAddress, destAddress);
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
