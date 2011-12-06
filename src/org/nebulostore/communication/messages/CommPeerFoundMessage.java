package org.nebulostore.communication.messages;

import org.nebulostore.communication.address.CommAddress;

/**
 * @author Marcin Walas
 */
public class CommPeerFoundMessage extends CommMessage {

  public CommPeerFoundMessage(CommAddress sourceAddress, CommAddress destAddress) {
    super(sourceAddress, destAddress);

  }
}
