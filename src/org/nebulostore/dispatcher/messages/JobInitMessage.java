package org.nebulostore.dispatcher.messages;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * This is a generic message to start a job via dispatcher.
 */
public class JobInitMessage extends Message {
  public JobInitMessage(String msgId, JobModule jobModule) {
    super(msgId);
    jobModule_ = jobModule;
  }

  public void accept(MessageVisitor visitor) throws NebuloException {
    visitor.visit(this);
  }

  public JobModule getHandler() {
    return jobModule_;
  }

  private JobModule jobModule_;
}
