package org.nebulostore.systest.broker.messages;

import org.nebulostore.appcore.messaging.Message;

/**
 * Message send to broker to get its context.
 *
 * @author szymonmatejczyk
 *
 */
public class GetBrokerContextMessage extends Message {
  private static final long serialVersionUID = 5150296318185156609L;

  public GetBrokerContextMessage(String jobId) {
    super(jobId);
  }
}
