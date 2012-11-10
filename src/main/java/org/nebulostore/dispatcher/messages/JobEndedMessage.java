package org.nebulostore.dispatcher.messages;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Worker thread sends this message to dispatcher before it dies to
 * indicate that the task has ended and can be removed from the thread map.
 *
 */
public class JobEndedMessage extends Message {
  private static final long serialVersionUID = 575456984015987666L;

  public JobEndedMessage(String jobID) {
    super(jobID);
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
