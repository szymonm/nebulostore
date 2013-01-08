package org.nebulostore.testing.communication.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author Grzegorz Milka
 */
public final class PongMessage extends CommMessage {
  private static final long serialVersionUID = 3661148334857628467L;
  /**
   * Peer who sent the pong.
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
    return String.format("PongMessage of id: %d, from (peerID): %d ," +
        "(sourceAddress): %s, to: %s.", id_, peerId_, getSourceAddress(),
        getDestinationAddress().toString());
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
