package org.nebulostore.async;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.appcore.modules.JobModule;
import org.nebulostore.async.messages.AsynchronousMessage;
import org.nebulostore.async.messages.StoreAsynchronousMessage;

/**
 * Module responsible for storing asynchrounous messages in this instance.
 * @author szymonmatejczyk
 */
public class StoreAsynchronousMessagesModule extends JobModule {

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  private AsyncMessagesContext context_;

  @Inject
  public void setDependencies(AsyncMessagesContext context) {
    context_ = context;
  }

  private SAMVisitor visitor_ = new SAMVisitor();

  /**
   * Visitor.
   * @author szymonmatejczyk
   */
  protected class SAMVisitor extends MessageVisitor<Void> {
    public Void visit(StoreAsynchronousMessage message) {
      // TODO(szm): Check if I should store this message.
      List<AsynchronousMessage> v = context_.getWaitingAsynchronousMessages().get(
          message.getRecipient());
      if (v == null) {
        List<AsynchronousMessage> list = new LinkedList<AsynchronousMessage>();
        list.add(message.getMessage());
        context_.getWaitingAsynchronousMessages().put(message.getRecipient(), list);
      } else {
        v.add(message.getMessage());
      }
      return null;
    }
  }

}
