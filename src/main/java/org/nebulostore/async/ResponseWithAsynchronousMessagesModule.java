package org.nebulostore.async;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.async.messages.AsynchronousMessagesMessage;
import org.nebulostore.async.messages.GetAsynchronousMessagesMessage;
import org.nebulostore.async.messages.GotAsynchronousMessagesMessage;
import org.nebulostore.broker.BrokerContext;

/**
 * Response for GetAsynchronousMessagesMessage.
 * @author szymonmatejczyk
 */
public class ResponseWithAsynchronousMessagesModule extends JobModule {
  private static Logger logger_ = Logger.getLogger(ResponseWithAsynchronousMessagesModule.class);

  private final BrokerContext context_;

  public ResponseWithAsynchronousMessagesModule(BrokerContext context) {
    super();
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
  private class Visitor extends MessageVisitor<Void> {
    @Override
    public Void visit(GetAsynchronousMessagesMessage message) {
      jobId_ = message.getId();
      // TODO(szm): prevent message flooding
      AsynchronousMessagesMessage reply = new AsynchronousMessagesMessage(message.getId(),
          message.getDestinationAddress(), message.getSourceAddress(),
          context_.waitingAsynchronousMessagesMap_.get(message.getRecipient()));
      networkQueue_.add(reply);

      context_.waitingForAck_.add(message.getRecipient());
      // TODO(szm): Timeout
      return null;
    }

    @Override
    public Void visit(GotAsynchronousMessagesMessage message) {
      // We assume that if Peer asks for AM to him, there won't be new messages
      // for him.
      if (context_.waitingForAck_.remove(message.getRecipient())) {
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
