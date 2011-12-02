package org.nebulostore.communication.messages;

import org.nebulostore.communication.address.CommAddress;

/**
 * @author Marcin Walas
 */
public class MsgCommPeerFound extends CommMessage {

  public MsgCommPeerFound(CommAddress sourceAddress, CommAddress destAddress) {
    super(sourceAddress, destAddress);

  }
}
