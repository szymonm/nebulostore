package org.nebulostore.communication.messages.bdbdht;

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

}
