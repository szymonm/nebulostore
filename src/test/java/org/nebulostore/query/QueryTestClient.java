package org.nebulostore.query;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.query.client.DQLClient;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
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

      String query = " GATHER" + "  LET peerAge = IF(LEAF_EXECUTION, LOAD_NOISE(\"peerData.xml\" , \"/peerData/age\", FALSE),  LOAD (\"peerData.xml\" , \"/peerData/age\", FALSE))  "
          + " FORWARD"  + " TO" + "  LOAD(\"peerData.xml\", \"/peerData/friends/friend\", TRUE)"
          + " REDUCE" + "  SUM(APPEND(peerAge,DQL_RESULTS))";

      DQLClient dqlClient = new DQLClient(query, 1);
      try {
        IDQLValue result = dqlClient.getResult(10);
        logger_.info("Got query results: " + result);
        phaseFinished();
      } catch (NebuloException e) {
        logger_.error(e);
      }
      return null;
    }

  }

}
