package org.nebulostore.testing.communication.messages;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.testing.communication.messages.PingMessage;

/**
 * @author Grzegorz Milka
 */
public final class PongMessage extends CommMessage {
  /**
   * Peer who sent the pong
   */
  private final int peerId_;
  /**
   * Message's id number. 
   * Id of the pong message it's responding to.
   */
  private final int id_;

  public PongMessage(CommAddress destAddress, int peerId,
      PingMessage ping) {
    super(null, null, destAddress);
    peerId_ = peerId;
    id_ = ping.getPingId();
  }

  public int getPeerId() {
    return peerId_;
  }

  public int getPingId() {
    return id_;
  }

  @Override
  public String toString() {
    return String.format("PongMessage of id: %d, from: %d", id_, peerId_);
  }
}