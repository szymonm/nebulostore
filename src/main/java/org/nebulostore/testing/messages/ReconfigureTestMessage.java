package org.nebulostore.testing.messages;

import java.util.Set;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

public class ReconfigureTestMessage extends CommMessage {

  private static final long serialVersionUID = -7407814653868855140L;
  private final Set<CommAddress> clients_;

  public ReconfigureTestMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, Set<CommAddress> clients) {
    super(jobId, sourceAddress, destAddress);
    clients_ = clients;
  }

  public Set<CommAddress> getClients() {
    return clients_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
