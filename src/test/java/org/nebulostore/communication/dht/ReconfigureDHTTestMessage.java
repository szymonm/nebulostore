package org.nebulostore.communication.dht;

import java.util.Set;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.messages.ReconfigurationMessage;

/**
 * @author grzegorzmilka
 */
public class ReconfigureDHTTestMessage extends ReconfigurationMessage {

  private static final long serialVersionUID = -2396699754933460615L;
  private final Set<CommAddress> clientsIn_;

  public ReconfigureDHTTestMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, Set<CommAddress> clientsOut,
      Set<CommAddress> clientsIn) {
    super(jobId, sourceAddress, destAddress, clientsOut);
    clientsIn_ = clientsIn;
  }

  public Set<CommAddress> getClientsIn() {
    return clientsIn_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
