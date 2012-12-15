package org.nebulostore.subscription.modules;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.subscription.messages.NotifySubscriberMessage;

/**
 * Author: rafalhryciuk.
 */
public class SubscriptionReceivedModule extends JobModule {

  private final SubscriptionReceivedMessageVisitor visitor_ =
      new SubscriptionReceivedMessageVisitor();


  public SubscriptionReceivedModule() {
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  /**
   * Message handler for received subscription notification.
   */
  private class SubscriptionReceivedMessageVisitor extends MessageVisitor<Void> {

    private final Logger logger_ =
        Logger.getLogger(SubscriptionReceivedMessageVisitor.class);

    @Override
    public Void visit(JobInitMessage message) throws NebuloException {
      return null;
    }

    @Override
    public Void visit(NotifySubscriberMessage message) throws NebuloException {
      //here You should put Your subscription notification handling code.
      logger_.info("==========================================================");
      logger_.info(message.getSubscriptionNotification());
      logger_.info("==========================================================");
      endJobModule();
      return null;
    }
  }
}
