package org.nebulostore.appcore.messages;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;

/**
 * Worker thread sends this message to dispatcher before it dies to
 * indicate that the task has ended and can be removed from the thread map.
 *
 */
public class JobEndedMessage extends Message {
  public JobEndedMessage(String msgID) {
    super(msgID);
  }
  public void visit(MessageVisitor visitor) {
    visitor.visit(this);
  }
}
