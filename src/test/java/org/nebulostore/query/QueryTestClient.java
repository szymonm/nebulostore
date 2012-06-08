package org.nebulostore.query;

import java.util.Map;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.query.client.DQLClient;
import org.nebulostore.query.executor.DQLExecutor;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.testing.TestingModule;
import org.nebulostore.testing.messages.NewPhaseMessage;
import org.nebulostore.testing.messages.ReconfigureTestMessage;

public class QueryTestClient extends TestingModule {

  private static Logger logger_ = Logger.getLogger(QueryTestClient.class);

  private static final long serialVersionUID = -5986566958498578754L;
  private final int testPhases_;

  private final int queryTimeout_;
  private final Map<Integer, QueryDescription> queriesToBeExecuted_;

  public QueryTestClient(String serverJobId, int testPhases,
      Map<Integer, QueryDescription> queriesToBeExecuted, int queryTimeout) {
    super(serverJobId);
    testPhases_ = testPhases;
    queriesToBeExecuted_ = queriesToBeExecuted;
    queryTimeout_ = queryTimeout;
  }

  @Override
  protected void initVisitors() {
    QueryVisitor queryVisitor = new QueryVisitor();
    visitors_ = new TestingModuleVisitor[testPhases_];
    visitors_[0] = new EmptyInitializationVisitor();
    visitors_[1] = new ConfigurationVisitor();
    for (int i = 2; i < testPhases_; i++) {
      visitors_[i] = queryVisitor;
    }
  }

  // TODO: EmptyConfigurationVisitor???
  final class ConfigurationVisitor extends EmptyInitializationVisitor {
    @Override
    public Void visit(ReconfigureTestMessage message) {
      logger_.info("Got reconfiguration message with clients set: " +
          message.getClients());
      phaseFinished();
      return null;
    }
  }

  final class QueryVisitor extends TestingModuleVisitor {

    @Override
    public Void visit(NewPhaseMessage message) {
      logger_.info("Issuing query in phase " + phase_);

      logger_.debug("queries to be executed: " + queriesToBeExecuted_);

      QueryDescription queryDescription = queriesToBeExecuted_.get(phase_ - 2);
      String query = queryDescription.getQuery();
      int maxDepth = queryDescription.getMaxDepth();

      DQLClient dqlClient = new DQLClient(query, maxDepth);
      try {
        IDQLValue result = dqlClient.getResult(queryTimeout_);
        logger_.info("Got query results: " + result);
      } catch (NebuloException e) {
        logger_.error(e);
      }
      logger_.info("Executing locally query...");
      LocalExecutor localExecutor = new LocalExecutor("./resources/test/query/pajek-40/");
      try {
        IDQLValue resultLocal = localExecutor.executeQuery(query, maxDepth, DQLExecutor.getInstance().getAllAppKeys());
        logger_.info("Got query results: " + resultLocal);
      } catch (NebuloException e) {
        logger_.error(e);
      }

      logger_.info("Finishing phase: " + phase_);
      phaseFinished();
      return null;
    }

  }

}
