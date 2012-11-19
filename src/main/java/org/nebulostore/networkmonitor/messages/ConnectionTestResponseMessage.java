package org.nebulostore.networkmonitor.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Response for ConnectionTestMessage.
 */
public class ConnectionTestResponseMessage extends CommMessage {
  private static final long serialVersionUID = 1003452365644646925L;

  public ConnectionTestResponseMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress) {
    super(jobId, sourceAddress, destAddress);
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}