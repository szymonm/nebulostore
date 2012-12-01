package org.nebulostore.communication.messages;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.messages.UserCommMessage;

/**
 * @author Marcin Walas
 */
public class DataExchangeMessage extends UserCommMessage {

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

  public String getPayload() {
    return payload_;
  }

  public int getCounterVal() {
    return counterVal_;
  }
}
