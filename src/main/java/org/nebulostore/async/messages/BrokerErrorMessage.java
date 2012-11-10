package org.nebulostore.async.messages;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Broker error message.
 * @author szymonmatejczyk
 */
public class BrokerErrorMessage extends Message {
  private static final long serialVersionUID = -4999136038489126015L;

  NebuloException error_;

  public NebuloException getError() {
    return error_;
  }

  public BrokerErrorMessage(NebuloException error) {
    super();
    error_ = error;
  }

  public BrokerErrorMessage(String jobId, NebuloException error) {
    super(jobId);
    error_ = error;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
