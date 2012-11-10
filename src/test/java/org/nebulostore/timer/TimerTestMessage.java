package org.nebulostore.timer;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Message used in this test.
 */
public class TimerTestMessage extends Message {
  private static final long serialVersionUID = 7915155806266242577L;
  public int code_;
  public TimerTestMessage(String jobId, int code) {
    jobId_ = jobId;
    code_ = code;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
