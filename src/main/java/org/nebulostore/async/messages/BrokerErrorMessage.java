package org.nebulostore.async.messages;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Broker error message.
 * @author szymonmatejczyk
 */
public class BrokerErrorMessage extends Message {
  NebuloException error_;

  public NebuloException getError() {
    return error_;
  }

  public BrokerErrorMessage(String jobId, NebuloException error) {
    super(jobId);
    error_ = error;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
