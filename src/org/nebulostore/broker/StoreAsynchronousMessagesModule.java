package org.nebulostore.broker;

import java.util.LinkedList;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.messages.broker.StoreAsynchronousMessage;
import org.nebulostore.communication.messages.broker.asynchronous.AsynchronousMessage;

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
  private class SAMVisitor extends MessageVisitor<Void> {
    public Void visit(StoreAsynchronousMessage message) {
      BrokerContext context = BrokerContext.getInstance();
      // TODO(szm): Check if I should store this message.
      LinkedList<AsynchronousMessage> v = context.waitingAsynchronousMessagesMap_.get(
          message.getRecipient());
      if (v == null) {
        LinkedList<AsynchronousMessage> list = new LinkedList<AsynchronousMessage>();
        list.add(message.getMessage());
        context.waitingAsynchronousMessagesMap_.put(message.getRecipient(), list);
      } else {
        v.add(message.getMessage());
      }
      return null;
    }
  }

}
