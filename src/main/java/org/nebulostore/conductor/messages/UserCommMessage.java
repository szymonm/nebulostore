package org.nebulostore.conductor.messages;

import java.io.Serializable;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Message class used to send test-specific messages.
 * @author bolek
 */
public class UserCommMessage extends CommMessage {
  private static final long serialVersionUID = -2865092973190890821L;
  protected final Serializable content_;

  public UserCommMessage(CommAddress sourceAddress, CommAddress destAddress) {
    super(sourceAddress, destAddress);
    content_ = null;
  }

  public UserCommMessage(String jobId, CommAddress destAddress, Serializable content) {
    super(jobId, null, destAddress);
    content_ = content;
  }

  public Serializable getContent() {
    return content_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
