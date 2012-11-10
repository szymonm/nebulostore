package org.nebulostore.testing.communication.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author Grzegorz Milka
 */
public final class PingMessage extends CommMessage {
  /**
   * Peer who sent the ping.
   */
  private final int peerId_;
  /**
   * Message's id number.
   * Used to distinguish old or pong messages.
   */
  private final int id_;
  private CommAddress rootSourceAddress_;

  public PingMessage(CommAddress rootSource, CommAddress destAddress,
          int peerId, int id) {
    super(null, null, destAddress);
    rootSourceAddress_ = rootSource;
    peerId_ = peerId;
    id_ = id;
  }

  public CommAddress getRootSourceAddress() {
    return rootSourceAddress_;
  }

  public int getPeerId() {
    return peerId_;
  }

  public int getPingId() {
    return id_;
  }

  @Override
  public String toString() {
    return String.format("PingMessage of id: %d, from: %d, to: %s",
            id_, peerId_, getDestinationAddress());
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
