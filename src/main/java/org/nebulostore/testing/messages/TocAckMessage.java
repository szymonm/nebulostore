package org.nebulostore.testing.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

public class TocAckMessage extends CommMessage {

  private static final long serialVersionUID = -5694999477137661326L;
  private final int phase_;

  public TocAckMessage(String jobId, CommAddress sourceAddress,
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
