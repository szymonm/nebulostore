package org.nebulostore.query.messages;

import java.math.BigInteger;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;

public class QueryResultsMessage extends CommMessage {

  private static final long serialVersionUID = 5801895535047010144L;
  private final BigInteger queryId_;
  private final IDQLValue result_;

  public QueryResultsMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, BigInteger queryId, IDQLValue result) {
    super(jobId, sourceAddress, destAddress);
    queryId_ = queryId;
    result_ = result;
  }

  public BigInteger getQueryId() {
    return queryId_;
  }

  public IDQLValue getResult() {
    return result_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
