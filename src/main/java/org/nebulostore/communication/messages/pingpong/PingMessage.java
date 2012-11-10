package org.nebulostore.communication.messages.pingpong;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author Marcin Walas
 */
public class PingMessage extends CommMessage {
  private static final long serialVersionUID = -7818808850794416626L;

  private final int number_;

  public PingMessage(String jobId, CommAddress destAddress, int number) {
    super(jobId, null, destAddress);
    number_ = number;
  }

  public int getNumber() {
    return number_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
