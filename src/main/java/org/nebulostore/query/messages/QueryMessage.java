package org.nebulostore.query.messages;

import java.math.BigInteger;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

public final class QueryMessage extends CommMessage {

  private static final long serialVersionUID = 188307597758799608L;

  private final String query_;
  private final BigInteger queryId_;
  private final String issuerJobId_;

  private final int currentDepth_;
  private final int maxDepth_;

  public QueryMessage(String dqlId, CommAddress sourceAddress,
      CommAddress destAddress, String issuerJobId, String query,
      BigInteger queryId, int currentDepth, int maxDepth) {
    super(dqlId, sourceAddress, destAddress);
    issuerJobId_ = issuerJobId;
    query_ = query;
    queryId_ = queryId;
    currentDepth_ = currentDepth;
    maxDepth_ = maxDepth;
  }

  public String getQuery() {
    return query_;
  }

  public BigInteger getQueryId() {
    return queryId_;
  }

  public String getIssuerJobId() {
    return issuerJobId_;
  }

  public int getCurrentDepth() {
    return currentDepth_;
  }

  public int getMaxDepth() {
    return maxDepth_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }



}
