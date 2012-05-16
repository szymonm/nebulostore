package org.nebulostore.query.messages;

import java.math.BigInteger;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

public class QueryAcceptedMessage extends CommMessage {

  private static final long serialVersionUID = 5471746741413314970L;
  private final BigInteger queryId_;

  public QueryAcceptedMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, BigInteger queryId) {
    super(jobId, sourceAddress, destAddress);
    queryId_ = queryId;
  }

  public BigInteger getQueryId() {
    return queryId_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
