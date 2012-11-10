package org.nebulostore.communication.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author Grzegorz Milka
 */
public class PeerDiscoveryMessage extends CommMessage {
  public PeerDiscoveryMessage() {
    super(null, null);
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "CommPeerFoundMessage about peer: " + getSourceAddress();
  }
}
