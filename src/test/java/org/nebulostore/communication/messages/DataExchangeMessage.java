package org.nebulostore.communication.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;

/**
 * @author Marcin Walas
 */
public class DataExchangeMessage extends CommMessage {

  private static final long serialVersionUID = 8490874879805594887L;
  private final String payload_;
  private final int phase_;
  private final int counterVal_;

  public DataExchangeMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, String payload, int phase, int counterVal) {
    super(jobId, sourceAddress, destAddress);
    payload_ = payload;
    phase_ = phase;
    counterVal_ = counterVal;
  }

  public int getPhase() {
    return phase_;
  }

  public int getCounterVal() {
    return counterVal_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

}
