package org.nebulostore.broker;

import java.util.List;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.messages.broker.AsynchronousMessagesMessage;
import org.nebulostore.communication.messages.broker.GetAsynchronousMessagesMessage;
import org.nebulostore.communication.messages.broker.GotAsynchronousMessagesMessage;
import org.nebulostore.communication.messages.broker.asynchronous.AsynchronousMessage;

/**
 * Response for GetAsynchronousMessagesMessage.
 * @author szymonmatejczyk
 */
public class ResponseWithAsynchronousMessagesModule extends JobModule {
  private static Logger logger_ = Logger.getLogger(ResponseWithAsynchronousMessagesModule.class);

  private BrokerContext context_;

  public ResponseWithAsynchronousMessagesModule(BrokerContext context) {
    super();
    context_ = context;
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  /* Message handling for broker */
  private Visitor visitor_ = new Visitor();

  /**
   * Visitor.
   * @author szymonmatejczyk
   */
  private class Visitor extends MessageVisitor<Void> {
    public Void visit(GetAsynchronousMessagesMessage message) {
      // TODO(szm): prevent message flooding
      List<AsynchronousMessage> messages = context_.waitingAsynchronousMessagesMap_.get(
          message.getRecipient());
      AsynchronousMessagesMessage reply = new AsynchronousMessagesMessage(message.getId(),
          message.getDestinationAddress(), message.getSourceAddress(),
          context_.waitingAsynchronousMessagesMap_.get(message.getRecipient()));
      networkQueue_.add(reply);

      context_.waitingForAck_.add(message.getRecipient());
      // TODO(szm): Timeout
      return null;
    }

    public Void visit(GotAsynchronousMessagesMessage message) {
      // We assume that if Peer asks for AM to him, there won't be new messages
      // for him.
      if (context_.waitingForAck_.remove(message.getRecipient())) {
        logger_.debug(message.getRecipient().toString() +
            " successfully downloaded " + "asynchronous messages.");
      } else {
        // TODO(szm): shouldn't be an error?
        logger_.warn("Got ACK, that shouldn't be sent.");
      }
      endJobModule();
      return null;
    }
  }
}
