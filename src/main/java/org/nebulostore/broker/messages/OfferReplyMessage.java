package org.nebulostore.broker.messages;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.broker.Broker;
import org.nebulostore.broker.Contract;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Reply to a contract offer.
 * @author bolek
 */
public class OfferReplyMessage extends CommMessage {
  private static final long serialVersionUID = -6854062479094405282L;
  private Contract contract_;
  private boolean result_;

  public OfferReplyMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, Contract contract, boolean result) {
    super(jobId, sourceAddress, destAddress);
    contract_ = contract;
    result_ = result;
  }

  public boolean getResult() {
    return result_;
  }

  public Contract getContract() {
    return contract_;
  }

  @Override
  public JobModule getHandler() throws NebuloException {
    return new Broker(jobId_, false);
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
