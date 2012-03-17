package org.nebulostore.communication.messages.kademlia;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Wrapper for packets sent over network by Angus implementation
 * of Kademlia protocol.
 *
 * @author Marcin Walas *
 */
public class KademliaMessage extends CommMessage {

  private final byte[] data_;

  public KademliaMessage(CommAddress sourceAddress, CommAddress destAddress,
      byte[] data) {
    super(sourceAddress, destAddress);
    data_ = data;
  }

  public byte[] getData() {
    return data_;
  }

  private static final long serialVersionUID = -7308102705214338084L;

}
