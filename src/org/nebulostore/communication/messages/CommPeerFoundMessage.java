package org.nebulostore.communication.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;

/**
 * @author Marcin Walas
 */
public class CommPeerFoundMessage extends CommMessage {

  public CommPeerFoundMessage(CommAddress sourceAddress, CommAddress destAddress) {
    super(sourceAddress, destAddress);
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
