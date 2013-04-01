package org.nebulostore.broker;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Module that forwards broker messages to broker singleton job.
 * @author Bolek Kulbabinski
 */
public class BrokerMessageForwarder extends JobModule {
  private Broker broker_;
  private Message message_;

  public BrokerMessageForwarder(Message message) {
    message_ = message;
  }

  public void setBroker(Broker broker) {
    broker_ = broker;
  }

  @Override
  public boolean isQuickNonBlockingTask() {
    return true;
  }

  @Override
  protected void initModule() {
    broker_.getInQueue().add(message_);
    endJobModule();
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
  }
}
