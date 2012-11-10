package org.nebulostore.communication.messages;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.broker.PeerFoundHandler;
import org.nebulostore.communication.address.CommAddress;

/**
 * @author Marcin Walas
 */
public class CommPeerFoundMessage extends CommMessage {
  private static final long serialVersionUID = -8231258035871545537L;

  public CommPeerFoundMessage(CommAddress sourceAddress, CommAddress destAddress) {
    super(sourceAddress, destAddress);
  }

  public CommPeerFoundMessage(String jobId, CommAddress sourceAddress, CommAddress destAddress) {
    super(jobId, sourceAddress, destAddress);
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  @Override
  public JobModule getHandler() throws NebuloException {
    return new PeerFoundHandler();
  }

  @Override
  public String toString() {
    return "CommPeerFoundMessage about peer: " + getSourceAddress();
  }
}
