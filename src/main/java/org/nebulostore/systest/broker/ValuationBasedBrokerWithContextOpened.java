package org.nebulostore.systest.broker;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.broker.BrokerContext;
import org.nebulostore.broker.ValuationBasedBroker;
import org.nebulostore.systest.broker.messages.BrokerContextMessage;
import org.nebulostore.systest.broker.messages.GetBrokerContextMessage;

public class ValuationBasedBrokerWithContextOpened extends ValuationBasedBroker {
  public final BrokerVisitor visitor_ = new ThisVisitor();
  
  public class ThisVisitor extends BrokerVisitor {
    public Void visit(GetBrokerContextMessage message) {
      outQueue_.add(new BrokerContextMessage(message.getId(),
          BrokerContext.getInstance()));
      return null;
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }
}
