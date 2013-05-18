package org.nebulostore.async;

import java.util.LinkedList;
import java.util.List;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.appcore.modules.JobModule;
import org.nebulostore.async.messages.AsynchronousMessage;
import org.nebulostore.async.messages.StoreAsynchronousMessage;
import org.nebulostore.broker.BrokerContext;

/**
 * Module responsible for storing asynchrounous messages in this instance.
 * @author szymonmatejczyk
 */
public class StoreAsynchronousMessagesModule extends JobModule {

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  private SAMVisitor visitor_ = new SAMVisitor();

  /**
   * Visitor.
   * @author szymonmatejczyk
   */
  protected static class SAMVisitor extends MessageVisitor<Void> {
    public Void visit(StoreAsynchronousMessage message) {
      BrokerContext context = BrokerContext.getInstance();
      // TODO(szm): Check if I should store this message.
      List<AsynchronousMessage> v = context.getWaitingAsynchronousMessages().get(
          message.getRecipient());
      if (v == null) {
        List<AsynchronousMessage> list = new LinkedList<AsynchronousMessage>();
        list.add(message.getMessage());
        context.getWaitingAsynchronousMessages().put(message.getRecipient(), list);
      } else {
        v.add(message.getMessage());
      }
      return null;
    }
  }

}
