package org.nebulostore.communication.dht.messages;

import java.util.Objects;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author Marcin Walas
 */
public class HolderAdvertisementMessage extends CommMessage {

  private static final long serialVersionUID = -4661627975684568150L;

  public HolderAdvertisementMessage(CommAddress sourceAddress,
      CommAddress destAddress) {
    super(sourceAddress, destAddress);
  }

  public HolderAdvertisementMessage(CommAddress destAddress) {
    super(null, destAddress);
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (getClass() != obj.getClass()) {
      return false;
    } else {
      HolderAdvertisementMessage ham = (HolderAdvertisementMessage) obj;
      return ham.getSourceAddress().equals(getSourceAddress()) &&
          ham.getDestinationAddress().equals(getDestinationAddress());
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSourceAddress(), getDestinationAddress());
  }
}
