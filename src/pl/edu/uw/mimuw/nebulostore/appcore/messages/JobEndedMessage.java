package pl.edu.uw.mimuw.nebulostore.appcore.messages;

import pl.edu.uw.mimuw.nebulostore.appcore.Message;
import pl.edu.uw.mimuw.nebulostore.appcore.MessageVisitor;

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
