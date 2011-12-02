package org.nebulostore.communication.messages;

import org.nebulostore.communication.address.CommAddress;

/**
 * @author Marcin Walas
 */
public class DiscoveryMessage extends CommMessage {

  private static final long serialVersionUID = 840036305899527524L;

  public DiscoveryMessage(CommAddress sourceAddress, CommAddress destAddress) {
    super(sourceAddress, destAddress);

  }

}
