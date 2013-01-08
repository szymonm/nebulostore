package org.nebulostore.communication.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author Marcin Walas
 */
public class CommSendDataMessage extends CommMessage {
  private static final long serialVersionUID = -1826257303182602743L;
  public String address_;
  public String data_;

  public CommSendDataMessage(String address, String data) {
    super(null, null);
    this.address_ = address;
    this.data_ = data;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
