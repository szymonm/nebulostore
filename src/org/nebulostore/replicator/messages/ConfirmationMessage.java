package org.nebulostore.replicator.messages;

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

  private static final long serialVersionUID = 4963514627405781252L;

}
