package org.nebulostore.appcore.model.subscription;

import java.io.Serializable;

import org.nebulostore.addressing.NebuloAddress;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: rh277703.
 */
public class SubscriptionNotification implements Serializable {

  private final NebuloAddress subscribedFileAddress_;

  private final NotificationReason notificationReason_;


  public SubscriptionNotification(NebuloAddress subscribedFileAddress,
                                  NotificationReason notificationReason) {
    this.subscribedFileAddress_ = checkNotNull(subscribedFileAddress);
    this.notificationReason_ = checkNotNull(notificationReason);
  }

  public NebuloAddress getSubscribedFileAddress_() {
    return subscribedFileAddress_;
  }

  public NotificationReason getNotificationReason_() {
    return notificationReason_;
  }

  @Override
  public String toString() {
    return "SubscriptionNotification{" +
        "subscribedFileAddress_=" + subscribedFileAddress_ +
        ", notificationReason_=" + notificationReason_ +
        '}';
  }

  public static enum NotificationReason {
    FILE_CHANGED, FILE_DELETED;
  }

}
