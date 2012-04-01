package org.nebulostore.broker;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.messages.CommPeerFoundMessage;

/**
 * Module handle CommPeerFoundMessage.
 * @author szymonmatejczyk
 */
public class PeerFoundHandler extends JobModule {

  private PeerFoundHandlerVisitor visitor_ = new PeerFoundHandlerVisitor();

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  /**
   * Visitor.
   * @author szymonmatejczyk
   */
  private class PeerFoundHandlerVisitor extends MessageVisitor<Void> {
    public Void visit(CommPeerFoundMessage message) {
      jobId_ = message.getId();
      NetworkContext context = NetworkContext.getInstance();
      context.addFoundPeer(message.getSourceAddress());
      endJobModule();
      return null;
    }
  }

}
