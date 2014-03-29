package org.nebulostore.conductor.messages;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.naming.CommAddress;

/**
 * Tests server sends this message when phase begins.
 * @author szymonmatejczyk
 */
public class TicMessage extends CommMessage {

  private static final long serialVersionUID = 8482635420673217310L;
  private final int phase_;

  public TicMessage(CommAddress sourceAddress, CommAddress destAddress, int phase) {
    super(sourceAddress, destAddress);
    phase_ = phase;
  }

  public TicMessage(String jobId, CommAddress sourceAddress,
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
