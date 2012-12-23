package org.nebulostore.subscription.modules;

import com.google.inject.Inject;
import org.apache.log4j.Logger;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.subscription.api.SubscriptionNotificationHandler;
import org.nebulostore.subscription.messages.NotifySubscriberMessage;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: rafalhryciuk.
 */
public class SubscriptionReceivedModule extends JobModule {

  private SubscriptionNotificationHandler notificationHandler_;

  private final SubscriptionReceivedMessageVisitor visitor_ =
      new SubscriptionReceivedMessageVisitor();


  public SubscriptionReceivedModule() {
  }

  @Inject
  public void setNotificationHandler(SubscriptionNotificationHandler notificationHandler) {
    this.notificationHandler_ = checkNotNull(notificationHandler);
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
      notificationHandler_.handleSubscriptionNotification(message.getSubscriptionNotification());
      endJobModule();
      return null;
    }
  }
}
