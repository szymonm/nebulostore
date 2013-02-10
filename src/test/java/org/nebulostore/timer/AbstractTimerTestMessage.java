package org.nebulostore.timer;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author bolek
 */
public abstract class AbstractTimerTestMessage extends Message {
  private static final long serialVersionUID = -6136353092587796967L;

  public AbstractTimerTestMessage(String jobId) {
    super(jobId);
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public abstract <R> R accept(TimerTestVisitor<R> visitor) throws NebuloException;
}
