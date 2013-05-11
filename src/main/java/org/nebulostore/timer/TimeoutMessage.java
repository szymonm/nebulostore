package org.nebulostore.timer;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.messaging.MessageVisitor;

/**
 * Message issued by Timer.
 * @author szymonmatejczyk
 */
public class TimeoutMessage extends Message {
  private static final long serialVersionUID = -8674965519068356105L;
  private String messageContent_;

  public TimeoutMessage(String jobID, String messageContent) {
    super(jobID);
    messageContent_ = messageContent;
  }

  public String getMessageContent() {
    return messageContent_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
