package org.nebulostore.query;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.query.client.DQLClient;
import org.nebulostore.testing.TestingModule;
import org.nebulostore.testing.messages.NewPhaseMessage;
import org.nebulostore.testing.messages.ReconfigureTestMessage;

public class QueryTestClient extends TestingModule {

  private static Logger logger_ = Logger.getLogger(QueryTestClient.class);

  private static final long serialVersionUID = -5986566958498578754L;
  private final int testPhases_;

  public QueryTestClient(String serverJobId, int testPhases) {
    super(serverJobId);
    testPhases_ = testPhases;
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

      String query = " GATHER" + "  LET peerData = LOAD (\"peerData.xml\" ) "
          + " FORWARD" + " MAX DEPTH 2" + " TO" + "  CREATE_LIST(1,2)"
          + " REDUCE" + "  CREATE_TUPLE(2,3)";

      DQLClient dqlClient = new DQLClient(query, 1);
      try {
        dqlClient.getResult(60);
      } catch (NebuloException e) {
        logger_.error(e);
      }
      return null;
    }

  }

}
