package org.nebulostore.conductor.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author szymonmatejczyk
 */
public class TicAckMessage extends CommMessage {
  private static final long serialVersionUID = -5694999477137661326L;
  private final int phase_;

  public TicAckMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, int phase) {
    super(jobId, sourceAddress, destAddress);
    phase_ = phase;
  }

  public int getPhase() {
    return phase_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

}
