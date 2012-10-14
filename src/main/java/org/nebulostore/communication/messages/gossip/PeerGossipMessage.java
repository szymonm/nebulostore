package org.nebulostore.communication.messages.gossip;

import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.gossip.PeerDescriptor;
import org.nebulostore.communication.messages.CommMessage;


/**
 * @author Grzegorz Milka
 */
//TODO(grzegorzmilka) serialVersionUID;
public class PeerGossipMessage extends CommMessage {
  /**
   * @author Grzegorz Milka
   */
  public static enum MessageType {
    PUSH, PULL
  }
  private EnumSet<MessageType> msgType_;
  private List<PeerDescriptor> buffer_;

  public PeerGossipMessage(CommAddress sourceAddress,
                           CommAddress destAddress,
                           Set<MessageType> msgType,
                           Collection<PeerDescriptor> buffer
                           ) throws IllegalArgumentException {
    super(sourceAddress, destAddress);
    if (msgType.size() == 0) {
      throw new IllegalArgumentException("Message type shouldn't be empty");
    }
    msgType_ = EnumSet.copyOf(msgType);
    buffer_ = new LinkedList<PeerDescriptor>(buffer);
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  public Set<MessageType> getMsgType() {
    return msgType_;
  }

  public List<PeerDescriptor> getBuffer() {
    return buffer_;
  }

  @Override
  public String toString() {
    return "PeerGossipMessage of type: " + msgType_ + " with buffer size: " +
      buffer_.size();
  }
}
