package org.nebulostore.communication.messages;

import java.util.List;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;

/**
 * @author Marcin Walas
 */
public class DiscoveryMessage extends CommMessage {

  private static final long serialVersionUID = 840036305899527524L;
  private final List<CommAddress> knownPeers_;

  public DiscoveryMessage(CommAddress sourceAddress,
                          CommAddress destAddress,
                          List<CommAddress> knownPeers) {
    super(sourceAddress, destAddress);
    knownPeers_ = knownPeers;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public List<CommAddress> getKnownPeers() {
    return knownPeers_;
  }
}
