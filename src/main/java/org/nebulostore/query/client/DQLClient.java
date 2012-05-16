package org.nebulostore.query.client;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.ReturningJobModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.query.executor.DQLExecutor;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.messages.QueryErrorMessage;
import org.nebulostore.query.messages.QueryMessage;
import org.nebulostore.query.messages.QueryResultsMessage;

public class DQLClient extends ReturningJobModule<IDQLValue> {
  private static Logger logger_ = Logger.getLogger(DQLClient.class);

  private final StateMachineVisitor visitor_;
  private final BigInteger queryId_;
  private final int maxDepth_;

  private final String query_;

  private static BlockingQueue<Message> dispatcherQueue_;

  public DQLClient(String query, int maxDepth) {
    query_ = query;
    maxDepth_ = maxDepth;
    queryId_ = CryptoUtils.getRandomId();
    visitor_ = new StateMachineVisitor();
    runThroughDispatcher(dispatcherQueue_);
  }

  /**
   * Visitor class that acts as a state machine realizing the procedure of
   * execution the query.
   */
  private class StateMachineVisitor extends MessageVisitor<Void> {

    public StateMachineVisitor() {
    }

    @Override
    public Void visit(JobInitMessage message) {
      logger_.debug("Sending query message to the DQLExecutor instance");
      networkQueue_.add(new QueryMessage(DQLExecutor.getInstance().getJobId(),
          null, CommunicationPeer.getPeerAddress(), jobId_, query_, queryId_,
          0, maxDepth_));
      return null;
    }

    @Override
    public Void visit(QueryResultsMessage message) {
      endWithSuccess(message.getResult());
      return null;
    }

    @Override
    public Void visit(QueryErrorMessage message) {
      endWithError(new NebuloException(message.getReason()));
      return null;
    }

  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  public static void setDispatcherQueue(BlockingQueue<Message> dispatcherQueue) {
    dispatcherQueue_ = dispatcherQueue;
  }
}
