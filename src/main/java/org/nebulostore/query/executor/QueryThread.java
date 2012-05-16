package org.nebulostore.query.executor;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.appcore.Message;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.query.language.interpreter.DQLInterpreter;
import org.nebulostore.query.language.interpreter.InterpreterState;
import org.nebulostore.query.language.interpreter.PreparedQuery;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IntegerValue;
import org.nebulostore.query.language.interpreter.datatypes.values.ListValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.messages.QueryAcceptedMessage;
import org.nebulostore.query.messages.QueryErrorMessage;
import org.nebulostore.query.messages.QueryMessage;
import org.nebulostore.query.messages.QueryResultsMessage;

public class QueryThread implements Runnable {

  private static Logger logger_ = Logger.getLogger(QueryThread.class);

  private final ExecutorContext executorContext_;
  private final BigInteger queryId_;
  private final String query_;
  private final DQLExecutor dqlExecutor_;
  private final DQLInterpreter interpreter_;
  private final BlockingQueue<Message> messagesQueue_;

  private final CommAddress sender_;
  private final int currentDepth_;
  private final int maxDepth_;

  private final String senderJobId_;

  private enum RemoteQueryState {
    Issued, AckReceived, Finished
  };

  private final Map<CommAddress, RemoteQueryState> queryState_;
  private final Map<CommAddress, Long> timeElapsed_;

  static private final long ackTimeout_ = 5000;
  static private final long queryTimeout_ = 10000;
  static private final long checkTimeout_ = 500;

  public QueryThread(DQLExecutor dqlExecutor, DQLInterpreter interpreter,
      ExecutorContext executorContext, BigInteger queryId, String query,
      String senderJobId, CommAddress sender, int currentDepth, int maxDepth) {
    dqlExecutor_ = dqlExecutor;
    interpreter_ = interpreter;
    executorContext_ = executorContext;
    queryId_ = queryId;
    query_ = query;

    senderJobId_ = senderJobId;
    sender_ = sender;
    currentDepth_ = currentDepth;
    maxDepth_ = maxDepth;
    queryState_ = new HashMap<CommAddress, RemoteQueryState>();
    timeElapsed_ = new HashMap<CommAddress, Long>();

    messagesQueue_ = new LinkedBlockingQueue<Message>();
  }

  @Override
  public void run() {

    try {
      PreparedQuery preparedQuery = interpreter_.prepareQuery(query_,
          maxDepth_, currentDepth_);

      InterpreterState state = interpreter_.createEmptyState();

      state = interpreter_.runGather(preparedQuery, state);

      // TODO: Czy ten warunek jest OK?
      if (currentDepth_ < maxDepth_) {
        state = interpreter_.runForward(preparedQuery, state);

        ListValue peersToForward = state.getPeersToForward();
        for (IDQLValue appKeyDQLValue : peersToForward) {
          int appKeyInt = ((IntegerValue) appKeyDQLValue).getValue();
          AppKey appKey = new AppKey(BigInteger.valueOf(appKeyInt));

          String remoteJobId = dqlExecutor_.getExecutorJobId(appKey);
          CommAddress peerAddress = dqlExecutor_.getExecutorCommAddress(appKey);

          if (remoteJobId == null || peerAddress == null) {
            logger_.error("Unable to forward query to peer with AppKey:" +
                appKey.toString());
            continue;
          }

          dqlExecutor_.putOnNetworkQueue(new QueryMessage(remoteJobId, null,
              peerAddress, dqlExecutor_.getJobId(), query_, queryId_,
              currentDepth_ + 1, maxDepth_));
          queryState_.put(peerAddress, RemoteQueryState.Issued);
        }

        // Wait for results;

        long lastTime = System.currentTimeMillis();
        while (!allFinished()) {
          try {
            Message message = messagesQueue_.poll(checkTimeout_,
                TimeUnit.MILLISECONDS);

            long currTime = System.currentTimeMillis();
            long delay = currTime - lastTime;
            lastTime = currTime;

            updateTimeouts(delay);

            if (message != null) {

              CommMessage commMessage = (CommMessage) message;

              if (queryState_.get(commMessage.getSourceAddress()) != RemoteQueryState.Finished) {

                if (message instanceof QueryAcceptedMessage) {
                  queryState_.put(commMessage.getSourceAddress(),
                      RemoteQueryState.AckReceived);
                }

                if (message instanceof QueryErrorMessage) {
                  QueryErrorMessage errorMessage = (QueryErrorMessage) message;
                  logger_.error("Received error with reason: " +
                      errorMessage.getReason());
                  queryState_.put(errorMessage.getSourceAddress(),
                      RemoteQueryState.Finished);
                }

                if (message instanceof QueryResultsMessage) {
                  QueryResultsMessage resultsMessage = (QueryResultsMessage) message;
                  state.addFromForward(resultsMessage.getResult());
                  queryState_.put(resultsMessage.getSourceAddress(),
                      RemoteQueryState.Finished);
                }

              }
            }

          } catch (InterruptedException e) {
            logger_.error("Interrupted exception received", e);
          }

        }
      }

      // moving to reduce...
      state = interpreter_.runReduce(preparedQuery, state);

      logger_.info("Sending back results of the query");
      dqlExecutor_.putOnNetworkQueue(new QueryResultsMessage(senderJobId_,
          null, sender_, queryId_, state.getReduceResult()));

    } catch (InterpreterException e) {
      logger_.error("Returning an error to previous hop peer", e);
      dqlExecutor_.putOnNetworkQueue(new QueryErrorMessage(senderJobId_, null,
          sender_, queryId_, "Interpreter exception occured"));
    }

    logger_.info("Finishing query...");

    // TODO: Running query on a interpreter
    dqlExecutor_.finishQueryThread(queryId_);
  }

  private void updateTimeouts(long delay) {
    for (CommAddress address : timeElapsed_.keySet()) {
      long timeout = timeElapsed_.get(address) + delay;
      timeElapsed_.put(address, timeout);
      if (timeout >= ackTimeout_ &&
          queryState_.get(address) == RemoteQueryState.Issued) {
        queryState_.put(address, RemoteQueryState.Finished);
      }
      if (timeout >= queryTimeout_ * (maxDepth_ - currentDepth_)) {
        queryState_.put(address, RemoteQueryState.Finished);
      }
    }
  }

  private boolean allFinished() {
    boolean allFinished = true;
    for (CommAddress address : queryState_.keySet()) {
      if (queryState_.get(address) != RemoteQueryState.Finished) {
        allFinished = false;
      }
    }
    return allFinished;
  }

  public void feedWithMessage(Message message) {
    messagesQueue_.add(message);
  }

}
