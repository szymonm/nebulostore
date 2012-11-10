package org.nebulostore.query.messages;

import java.math.BigInteger;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

public class QueryErrorMessage extends CommMessage {

  private static final long serialVersionUID = -3907768774571638449L;

  private final String reason_;
  private final BigInteger queryId_;

  public QueryErrorMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, BigInteger queryId, String reason) {
    super(jobId, sourceAddress, destAddress);
    queryId_ = queryId;
    reason_ = reason;
  }

  public String getReason() {
    return reason_;
  }

  public BigInteger getQueryId() {
    return queryId_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
