package org.nebulostore.broker;

import com.google.inject.Inject;

import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.modules.JobModule;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.networkmonitor.NetworkContext;
import org.nebulostore.timer.MessageGenerator;

/**
 * Broker is always a singleton job. See BrokerMessageForwarder.
 * @author Bolek Kulbabinski
 */
public abstract class Broker extends JobModule {
  protected CommAddress myAddress_;

  @Inject
  void setCommAddress(CommAddress myAddress) {
    myAddress_ = myAddress;
  }

  @Override
  protected void initModule() {
    subscribeForCommPeerFoundEvents();
  }

  protected void subscribeForCommPeerFoundEvents() {
    NetworkContext.getInstance().addContextChangeMessageGenerator(new MessageGenerator() {
      @Override
      public Message generate() {
        return new CommPeerFoundMessage(jobId_, null, null);
      }
    });
  }
}
