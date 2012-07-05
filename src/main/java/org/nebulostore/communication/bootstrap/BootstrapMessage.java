package org.nebulostore.communication.bootstrap;

import org.nebulostore.communication.address.CommAddress;

public class BootstrapMessage implements Serializable {
  private static enum BootstrapMessageType {
    KEEP_ALIVE, PEER_DISCOVERY, PEER_INFO
  }
  private BootstrapMessageType messageType_;
  private CommAddress peerAddress_ = null;
  /**
   * Create KEEP_ALIVE or PEER_DISCOVERY bootstrap message.
   */
  public BootstrapMessage(BootstrapMessageType messageType) {
    if(messageType == PEER_INFO)
      throw new IllegalArgumentException("To create PEER_INFO message use " +
          "CommAddress constructor.");

    messageType_ = messageType;
  }

  /**
   * Create PEER_INFO bootstrap message.
   */
  public BootstrapMessage(CommAddress peerAddress) {
    messageType_ = PEER_INFO;
    peerAddress_ = peerAddress;
  }

  public BootstrapMessageType getType() {
    return messageType_;
  }

  public CommAddress getAddress() {
    return peerAddress_;
  }
}
