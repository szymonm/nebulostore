package org.nebulostore.systest.broker.messages;

import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.broker.BrokerContext;

/**
 * Message send by Broker with its context.
 *
 * @author szymonmatejczyk
 */
public class BrokerContextMessage extends Message {
  private static final long serialVersionUID = -6196721928597455534L;

  private final BrokerContext brokerContext_;

  public BrokerContextMessage(String jobId, BrokerContext brokerContext) {
    super(jobId);
    brokerContext_ = brokerContext;
  }

  public BrokerContext getBrokerContext() {
    return brokerContext_;
  }
}
