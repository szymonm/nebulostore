package org.nebulostore.dispatcher.messages;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * This is a generic message to start a job via dispatcher.
 */
public class JobInitMessage extends Message {
  private static final long serialVersionUID = 2482967055598180345L;
  private final transient JobModule jobModule_;

  public JobInitMessage(JobModule jobModule) {
    jobModule_ = jobModule;
  }

  @Deprecated
  public JobInitMessage(String jobId, JobModule jobModule) {
    super(jobId);
    jobModule_ = jobModule;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  @Override
  public JobModule getHandler() {
    return jobModule_;
  }
}
