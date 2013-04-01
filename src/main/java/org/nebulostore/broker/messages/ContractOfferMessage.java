package org.nebulostore.broker.messages;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.broker.BrokerMessageForwarder;
import org.nebulostore.broker.Contract;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Broker's contract offer.
 * @author bolek
 */
public class ContractOfferMessage extends CommMessage {
  private static final long serialVersionUID = -578571854606199914L;
  private Contract contract_;

  public ContractOfferMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, Contract contract) {
    super(jobId, sourceAddress, destAddress);
    contract_ = contract;
  }

  public Contract getContract() {
    return contract_;
  }

  @Override
  public JobModule getHandler() throws NebuloException {
    return new BrokerMessageForwarder(this);
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
