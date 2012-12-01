package org.nebulostore.communication.messages;

import java.util.Set;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.messages.ReconfigurationMessage;

/**
 */
public class ReconfigureMessagesTestMessage extends ReconfigurationMessage {

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
}
