package org.nebulostore.broker.messages;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.messaging.MessageVisitor;

/**
 * Message send by an instance to itself to start ImproveContractsModule.
 *
 * @author szymonmatejczyk
 *
 */
public class ImproveContractsMessage extends Message {
  private static final long serialVersionUID = 3986327905857030018L;

  public ImproveContractsMessage(String jobId) {
    super(jobId);
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

}
