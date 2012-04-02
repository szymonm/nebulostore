package org.nebulostore.async.messages;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.async.RetrieveAsynchronousMessagesModule;


/**
 * Message send to broker to start downloading asynchronous messages.
 * @author szymonmatejczyk
 */
public class GetAsynchronousMessagesIn extends Message {
  private static final long serialVersionUID = -750054447042250966L;

  public GetAsynchronousMessagesIn() {
    super();
  }

  public GetAsynchronousMessagesIn(String jobID) {
    super(jobID);
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  @Override
  public JobModule getHandler() throws NebuloException {
    return new RetrieveAsynchronousMessagesModule();
  }
}
