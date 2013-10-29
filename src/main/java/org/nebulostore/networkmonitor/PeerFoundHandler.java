package org.nebulostore.networkmonitor;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.appcore.modules.JobModule;
import org.nebulostore.communication.messages.CommPeerFoundMessage;

/**
 * Module handle CommPeerFoundMessage.
 * @author szymonmatejczyk
 */
public class PeerFoundHandler extends JobModule {

  private final PeerFoundHandlerVisitor visitor_ = new PeerFoundHandlerVisitor();

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  /**
   * Visitor.
   * @author szymonmatejczyk
   */
  protected class PeerFoundHandlerVisitor extends MessageVisitor<Void> {
    public Void visit(CommPeerFoundMessage message) {
      jobId_ = message.getId();
      NetworkContext context = NetworkContext.getInstance();
      context.addFoundPeer(message.getSourceAddress());
      endJobModule();
      return null;
    }
  }

}