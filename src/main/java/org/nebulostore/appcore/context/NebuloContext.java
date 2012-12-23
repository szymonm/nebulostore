package org.nebulostore.appcore.context;


import com.google.inject.AbstractModule;
import org.nebulostore.subscription.api.SimpleSubscriptionNotificationHandler;
import org.nebulostore.subscription.api.SubscriptionNotificationHandler;

/**
 * Guice context configuration for nebulostore.
 */
public class NebuloContext extends AbstractModule {

  @Override
  protected void configure() {
    bind(SubscriptionNotificationHandler.class).to(SimpleSubscriptionNotificationHandler.class);
  }

}
