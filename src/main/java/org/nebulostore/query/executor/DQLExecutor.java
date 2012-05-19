package org.nebulostore.query.executor;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.broker.NetworkContext;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.query.language.interpreter.DQLInterpreter;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.messages.GossipExecutorsMessage;
import org.nebulostore.query.messages.QueryAcceptedMessage;
import org.nebulostore.query.messages.QueryErrorMessage;
import org.nebulostore.query.messages.QueryMessage;
import org.nebulostore.query.messages.QueryResultsMessage;

public class DQLExecutor extends JobModule {

  private static Logger logger_ = Logger.getLogger(DQLExecutor.class);
  private static int THREAD_POOL_SIZE = 5;

  private final ExecutorService threadPool_;
  private final ExecutorContext executorContext_;
  private final ExecutorVisitor visitor_;
  private final DQLInterpreter interpreter_;
  private int runningQueries_;

  private final Map<BigInteger, QueryThread> queryThreads_;
  private final Map<AppKey, CommAddress> remoteExecutorsAddresses_;
  private final Map<AppKey, String> remoteExecutorsJobIds_;

  private final Set<IDQLValue> returnedValues_;

  private final Set<BigInteger> executedQueries_;

  private static DQLExecutor instance_;

  public DQLExecutor(String dqlJobId, boolean permanentInstance) {
    super(dqlJobId);
    // this bounds maximum number of sim. executed queries
    threadPool_ = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    executorContext_ = new ExecutorContext();
    visitor_ = new ExecutorVisitor(this);
    interpreter_ = new DQLInterpreter(executorContext_);
    runningQueries_ = 0;

    queryThreads_ = new HashMap<BigInteger, QueryThread>();
    remoteExecutorsAddresses_ = new HashMap<AppKey, CommAddress>();
    remoteExecutorsJobIds_ = new HashMap<AppKey, String>();

    executedQueries_ = new HashSet<BigInteger>();

    if (permanentInstance) {
      instance_ = this;
      NetworkContext.getInstance().addContextChangeMessage(
          new CommPeerFoundMessage(dqlJobId, null, null));
    }

    returnedValues_ = new HashSet<IDQLValue>();
  }

  static public DQLExecutor getInstance() {
    return instance_;
  }

  /**
   * Visitor.
   */
  private class ExecutorVisitor extends MessageVisitor<Void> {

    private final DQLExecutor dqlExecutor_;

    public ExecutorVisitor(DQLExecutor dqlExecutor) {
      dqlExecutor_ = dqlExecutor;
    }

    @Override
    public Void visit(JobInitMessage message) {
      // Currently empty.
      return null;
    }

    @Override
    public Void visit(QueryMessage message) {

      logger_.info("Got QueryMessage for QueryId: " + message.getQueryId() +
          " and query: " + message.getQuery() + " depth (" +
          message.getCurrentDepth() + "/" + message.getMaxDepth() + ")");
      synchronized (queryThreads_) {
        if (runningQueries_ < THREAD_POOL_SIZE &&
            !executedQueries_.contains(message.getQueryId())) {

          executedQueries_.add(message.getQueryId());

          logger_.info("Accepting query from : " + message.getSourceAddress() +
              " of contents: " + message.getQuery());
          runningQueries_++;
          QueryThread queryThread = new QueryThread(dqlExecutor_, interpreter_,
              executorContext_, message.getQueryId(), message.getQuery(),
              message.getIssuerJobId(), message.getSourceAddress(),
              message.getCurrentDepth(), message.getMaxDepth());
          queryThreads_.put(message.getQueryId(), queryThread);
          threadPool_.submit(queryThread);

          networkQueue_.add(new QueryAcceptedMessage(message.getId(), null,
              message.getSourceAddress(), message.getQueryId()));
        } else {
          networkQueue_.add(new QueryErrorMessage(message.getId(), null,
              message.getSourceAddress(), message.getQueryId(),
              "Insufficient resources to run issued query"));
        }
      }
      return null;
    }

    @Override
    public Void visit(QueryAcceptedMessage message) {
      logger_.info("Got QueryAcceptedMessage for QueryId: " +
          message.getQueryId() + " from : " + message.getSourceAddress());
      synchronized (queryThreads_) {
        if (!queryThreads_.containsKey(message.getQueryId())) {
          logger_.warn("Got " + message.toString() + " and query for " +
              message.getQueryId() + " already finished");
          return null;
        }
        queryThreads_.get(message.getQueryId()).feedWithMessage(message);
      }
      return null;
    }

    @Override
    public Void visit(QueryErrorMessage message) {
      logger_.info("Got QueryErrorMessage for QueryId: " +
          message.getQueryId() + " and reason: " + message.getReason());
      synchronized (queryThreads_) {
        if (!queryThreads_.containsKey(message.getQueryId())) {
          logger_.warn("Got " + message.toString() + " and query for " +
              message.getQueryId() + " already finished");
          return null;
        }
        queryThreads_.get(message.getQueryId()).feedWithMessage(message);
      }
      return null;
    }

    @Override
    public Void visit(QueryResultsMessage message) {
      logger_.info("Got QueryResultsMessage for QueryId: " +
          message.getQueryId() + " and contents: " + message.getResult());
      synchronized (queryThreads_) {
        if (!queryThreads_.containsKey(message.getQueryId())) {
          logger_.warn("Got " + message.toString() + " and query for " +
              message.getQueryId() + " already finished");
          return null;
        }
        queryThreads_.get(message.getQueryId()).feedWithMessage(message);
      }
      return null;
    }

    @Override
    public Void visit(CommPeerFoundMessage message) {
      Vector<CommAddress> knownPeers = NetworkContext.getInstance()
          .getKnownPeers();
      // adding myself
      addRemoteExecutor(ApiFacade.getAppKey(), jobId_,
          CommunicationPeer.getPeerAddress());

      synchronized (DQLExecutor.getInstance().remoteExecutorsAddresses_) {
        if (!DQLExecutor.getInstance().remoteExecutorsAddresses_
            .containsValue(message.getSourceAddress())) {

          logger_.info("Gossping known executors: " +
              dqlExecutor_.remoteExecutorsJobIds_ + "; " +
              dqlExecutor_.remoteExecutorsAddresses_);

          for (CommAddress address : knownPeers) {
            if (!address.equals(CommunicationPeer.getPeerAddress())) {
              networkQueue_.add(new GossipExecutorsMessage(CryptoUtils
                  .getRandomId().toString(), null, address,
                  new HashMap<AppKey, CommAddress>(
                      dqlExecutor_.remoteExecutorsAddresses_),
                      new HashMap<AppKey, String>(
                          dqlExecutor_.remoteExecutorsJobIds_)));
            }

          }
        }

      }
      return null;
    }

    @Override
    public Void visit(GossipExecutorsMessage message) {
      logger_.info("Gossip executors message received. feeding...");
      synchronized (DQLExecutor.getInstance().remoteExecutorsAddresses_) {
        for (AppKey appKey : message.getExecutorsAddresses().keySet()) {
          DQLExecutor.getInstance().addRemoteExecutor(appKey,
              message.getExecutorsJobIds().get(appKey),
              message.getExecutorsAddresses().get(appKey));
        }
      }
      endJobModule();
      return null;
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  public void finishQueryThread(BigInteger queryId_) {
    logger_.info("Query thread for " + queryId_ + " finishing...");
    synchronized (queryThreads_) {
      queryThreads_.remove(queryId_);
      runningQueries_--;
    }
  }

  public void addRemoteExecutor(AppKey appKey, String jobId, CommAddress address) {
    synchronized (DQLExecutor.getInstance().remoteExecutorsAddresses_) {
      if (!remoteExecutorsAddresses_.containsKey(appKey)) {
        remoteExecutorsAddresses_.put(appKey, address);
        remoteExecutorsJobIds_.put(appKey, jobId);
      }
    }
  }

  public void putOnNetworkQueue(Message message) {
    networkQueue_.add(message);
  }

  public CommAddress getExecutorCommAddress(AppKey appKey) {
    logger_.debug("Getting commAddress for appKey: " + appKey);
    logger_.debug("remoteCommAddresses_: " + remoteExecutorsAddresses_);
    return remoteExecutorsAddresses_.get(appKey);
  }

  public String getExecutorJobId(AppKey appKey) {
    logger_.debug("Getting jobId for appKey: " + appKey);
    logger_.debug("remoteJobIds: " + remoteExecutorsJobIds_);
    return remoteExecutorsJobIds_.get(appKey);
  }

  public void sendQueryResults(QueryResultsMessage queryResultsMessage) {

    boolean canBeSent = true;
    synchronized (returnedValues_) {
      logger_.info("Checking whether value: " +
          queryResultsMessage.getResult() + " can be sent against set: " +
          returnedValues_);
      for (IDQLValue value : returnedValues_) {
        try {
          if (!queryResultsMessage
              .getResult()
              .getPrivacyLevel()
              .compose(value.getPrivacyLevel(),
                  queryResultsMessage.getResult(), value,
                  queryResultsMessage.getResult()).canBeSent()) {
            canBeSent = false;
            break;
          }
        } catch (InterpreterException e) {
          logger_.error("Error in checking privacy levels: ", e);
        }
      }

      if (canBeSent) {
        logger_.info("It is OK to send this result back: " +
            queryResultsMessage.getResult());

        networkQueue_.add(queryResultsMessage);
      } else {
        logger_
        .info("Returning the value may compromise the privacy. returning error instead.");
        networkQueue_
        .add(new QueryErrorMessage(queryResultsMessage.getId(), null,
            queryResultsMessage.getDestinationAddress(),
            queryResultsMessage.getQueryId(),
            "Returning the results of this query may compromise data privacy."));
      }

    }

  }
}
