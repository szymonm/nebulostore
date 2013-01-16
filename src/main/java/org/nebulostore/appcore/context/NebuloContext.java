package org.nebulostore.appcore.context;

import com.google.inject.AbstractModule;

import org.apache.commons.configuration.XMLConfiguration;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.subscription.api.SimpleSubscriptionNotificationHandler;
import org.nebulostore.subscription.api.SubscriptionNotificationHandler;

/**
 * Guice context configuration for nebulostore.
 */
public class NebuloContext extends AbstractModule {
  private final XMLConfiguration config_;
  private final AppKey appKey_;

  public NebuloContext(AppKey appKey, XMLConfiguration config) {
    appKey_ = appKey;
    config_ = config;
  }

  @Override
  protected void configure() {
    bind(SubscriptionNotificationHandler.class).to(SimpleSubscriptionNotificationHandler.class);
    bind(XMLConfiguration.class).toInstance(config_);
    bind(AppKey.class).toInstance(appKey_);
  }
}
