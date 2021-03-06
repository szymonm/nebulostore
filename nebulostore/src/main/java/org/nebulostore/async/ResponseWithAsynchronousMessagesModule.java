package org.nebulostore.async;

import com.google.inject.Inject;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.appcore.modules.JobModule;
import org.nebulostore.async.messages.AsynchronousMessagesMessage;
import org.nebulostore.async.messages.GetAsynchronousMessagesMessage;
import org.nebulostore.async.messages.GotAsynchronousMessagesMessage;


/**
 * Response for GetAsynchronousMessagesMessage.
 * @author szymonmatejczyk
 */
public class ResponseWithAsynchronousMessagesModule extends JobModule {
  private static Logger logger_ = Logger.getLogger(ResponseWithAsynchronousMessagesModule.class);

  private AsyncMessagesContext context_;

  @Inject
  public void setDependencies(AsyncMessagesContext context) {
    context_ = context;
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  /* Message handling for broker */
  private final Visitor visitor_ = new Visitor();

  /**
   * Visitor.
   * @author szymonmatejczyk
   */
  protected class Visitor extends MessageVisitor<Void> {
    public Void visit(GetAsynchronousMessagesMessage message) {
      jobId_ = message.getId();
      // TODO(szm): prevent message flooding
      AsynchronousMessagesMessage reply = new AsynchronousMessagesMessage(message.getId(),
          message.getDestinationAddress(), message.getSourceAddress(),
          context_.getWaitingAsynchronousMessages().get(message.getRecipient()));
      networkQueue_.add(reply);

      context_.getWaitingForAck().add(message.getRecipient());
      // TODO(szm): Timeout
      return null;
    }

    public Void visit(GotAsynchronousMessagesMessage message) {
      // We assume that if Peer asks for AM to him, there won't be new messages
      // for him.
      if (context_.getWaitingForAck().remove(message.getRecipient())) {
        logger_.debug(message.getRecipient().toString() +
            " successfully downloaded " + "asynchronous messages.");
      } else {
        logger_.warn("Got ACK, that shouldn't be sent.");
      }
      endJobModule();
      return null;
    }
  }
}
