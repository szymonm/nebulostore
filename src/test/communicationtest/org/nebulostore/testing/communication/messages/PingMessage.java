package org.nebulostore.testing.communication.messages;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author Grzegorz Milka
 */
public final class PingMessage extends CommMessage {
  /**
   * Peer who sent the ping
   */
  private final int peerId_;
  /**
   * Message's id number. 
   * Used to distinguish old or pong messages.
   */
  private final int id_;

  public PingMessage(CommAddress destAddress, int peerId, int id) {
    super(null, null, destAddress);
    peerId_ = peerId;
    id_ = id;
  }

  public int getPeerId() {
    return peerId_;
  }

  public int getPingId() {
    return id_;
  }

  @Override
  public String toString() {
    return String.format("PingMessage of id: %d, from: %d", id_, peerId_);
  }
}
