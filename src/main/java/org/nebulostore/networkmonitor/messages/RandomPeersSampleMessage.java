package org.nebulostore.networkmonitor.messages;

import java.util.Set;

import com.rits.cloning.Cloner;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.appcore.modules.JobModule;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.networkmonitor.RandomPeersGossipingModule;

/**
 * Message containing set of random peers. Used by gossiping random peers protocol.
 * @author szymonmatejczyk
 */
public class RandomPeersSampleMessage extends CommMessage {
  public RandomPeersSampleMessage(CommAddress sourceAddress,
      CommAddress destAddress, Set<CommAddress> peersSet) {
    super(sourceAddress, destAddress);
    Cloner c = new Cloner();
    peersSet_ = c.deepClone(peersSet);
  }

  private static final long serialVersionUID = 7275803958168737858L;

  private final Set<CommAddress> peersSet_;

  public Set<CommAddress> getPeersSet() {
    return peersSet_;
  }

  @Override
  public JobModule getHandler() throws NebuloException {
    return new RandomPeersGossipingModule();
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
