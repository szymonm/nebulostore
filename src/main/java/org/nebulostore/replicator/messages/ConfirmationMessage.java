package org.nebulostore.replicator.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Message send between Replicator and Network modulu to idicate that object was
 * succesfully stored.
 * @author szymonmatejczyk
 */
public class ConfirmationMessage extends CommMessage {
  public ConfirmationMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress) {
    super(jobId, sourceAddress, destAddress);
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  private static final long serialVersionUID = 4963514627405781252L;

}
