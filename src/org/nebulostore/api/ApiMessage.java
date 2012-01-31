package org.nebulostore.api;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Base class for messages passed from API job module to waiting user API call.
 */
public abstract class ApiMessage extends Message {
  public ApiMessage() {
    // This messages are (currently) not passed via Dispatcher.
    super("unused_job_id");
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
