package org.nebulostore.timer;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;


/**
 * Test initialization message.
 */
public class InitSimpleTimerTestMessage extends Message {
  private static final long serialVersionUID = 1714324074362911323L;

  SimpleTimerTestModule handler_ = new SimpleTimerTestModule();
  public JobModule getHandler() throws NebuloException {
    return handler_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
