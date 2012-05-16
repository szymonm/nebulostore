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
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.broker.NetworkContext;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.query.language.interpreter.DQLInterpreter;
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

  private final Set<BigInteger> executedQueries_;

  private static DQLExecutor instance_;

  public DQLExecutor(String dqlJobId, boolean permanentInstance) {
    super(dqlJobId);
    // this bounds maximum number of sim. executed queries
    threadPool_ = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    executorContext_ = new ExecutorContext("");
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
      logger_.info("Gossping known executors: " +
          dqlExecutor_.remoteExecutorsJobIds_);
      for (CommAddress address : knownPeers) {
        networkQueue_.add(new GossipExecutorsMessage(CryptoUtils.getRandomId()
            .toString(), null, address, dqlExecutor_.remoteExecutorsAddresses_,
            dqlExecutor_.remoteExecutorsJobIds_));
      }
      return null;
    }

    @Override
    public Void visit(GossipExecutorsMessage message) {
      logger_.info("Gossip executors message received. feeding...");
      for (AppKey appKey : message.getExecutorsAddresses().keySet()) {
        DQLExecutor.getInstance().addRemoteExecutor(appKey,
            message.getExecutorsJobIds().get(appKey),
            message.getExecutorsAddresses().get(appKey));
      }
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
    if (!remoteExecutorsAddresses_.containsKey(appKey)) {
      remoteExecutorsAddresses_.put(appKey, address);
      remoteExecutorsJobIds_.put(appKey, jobId);
    }
  }

  public void putOnNetworkQueue(Message message) {
    networkQueue_.add(message);
  }

  public CommAddress getExecutorCommAddress(AppKey appKey) {
    return remoteExecutorsAddresses_.get(appKey);
  }

  public String getExecutorJobId(AppKey appKey) {
    return remoteExecutorsJobIds_.get(appKey);
  }

}
