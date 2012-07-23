package org.nebulostore.communication.bootstrap;

import java.io.Serializable;
import org.nebulostore.communication.address.CommAddress;

/**
 * Bootstrap Hello Message.
 * @author Grzegorz Milka
 */
//TODO-GM serialUID
public class BootstrapMessage implements Serializable {
  private CommAddress peerAddress_ = null;

  /**
   * Create PEER_INFO bootstrap message.
   */
  public BootstrapMessage(CommAddress peerAddress) {
    peerAddress_ = peerAddress;
  }

  public CommAddress getPeerAddress() {
    return peerAddress_;
  }
}
