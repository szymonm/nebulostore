package org.nebulostore.conductor.messages;

import java.io.Serializable;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Message class used to send test-specific messages.
 * @author Bolek Kulbabinski
 */
public class UserCommMessage extends CommMessage {
  private static final long serialVersionUID = -2865092973190890821L;
  protected final Serializable content_;
  protected final int phase_;

  public UserCommMessage(String jobId, CommAddress destAddress, Serializable content, int phase) {
    super(jobId, null, destAddress);
    content_ = content;
    phase_ = phase;
  }

  public Serializable getContent() {
    return content_;
  }

  public int getPhase() {
    return phase_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
