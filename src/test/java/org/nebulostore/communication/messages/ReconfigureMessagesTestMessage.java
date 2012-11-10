package org.nebulostore.communication.messages;

import java.util.Set;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.testing.messages.ReconfigureTestMessage;

/**
 */
public class ReconfigureMessagesTestMessage extends ReconfigureTestMessage {

  private static final long serialVersionUID = 2088768307390406436L;

  private final int expectedInClients_;

  public ReconfigureMessagesTestMessage(String jobId,
      CommAddress sourceAddress, CommAddress destAddress,
      Set<CommAddress> clients, int expectedInClients) {
    super(jobId, sourceAddress, destAddress, clients);
    expectedInClients_ = expectedInClients;
  }

  public int getExpectedInClients() {
    return expectedInClients_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

}
