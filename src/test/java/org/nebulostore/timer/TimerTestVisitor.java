package org.nebulostore.timer;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Class used to visit local test messages.
 * @author bolek
 * @param <R>
 */
public class TimerTestVisitor<R> extends MessageVisitor<R> {
  public R visit(TimerTestMessage message) throws NebuloException {
    return visit((Message) message);
  }

  public R visit(InitSimpleTimerTestMessage message) throws NebuloException {
    return visit((Message) message);
  }
}
