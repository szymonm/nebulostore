package org.nebulostore.communication.bootstrap;

import java.io.Serializable;
import org.nebulostore.communication.address.CommAddress;
import static org.nebulostore.communication.bootstrap.BootstrapMessageType.*;

public class BootstrapMessage implements Serializable {
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
    peerAddress_ = null;
  }

  public BootstrapMessage(BootstrapMessageType messageType, CommAddress peerAddress) {
    messageType_ = messageType;
    peerAddress_ = peerAddress;
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

  public CommAddress getPeerAddress() {
    return peerAddress_;
  }
}
