package org.nebulostore.query.language.interpreter.test.standalone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.language.interpreter.DQLInterpreter;
import org.nebulostore.query.language.interpreter.InterpreterState;
import org.nebulostore.query.language.interpreter.PreparedQuery;
import org.nebulostore.query.language.interpreter.datatypes.values.IntegerValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.language.interpreter.test.functions.CheckBool;
import org.nebulostore.query.language.interpreter.test.functions.CheckPrivacyLevel;
import org.nebulostore.query.language.interpreter.test.functions.PurgeSources;
import org.nebulostore.query.language.interpreter.test.functions.ShouldFail;
import org.nebulostore.query.privacy.level.PublicOthers;

public class AuditTest {

  private static final int TEST_NUM = 1000;
  private static Log log = LogFactory.getLog(AuditTest.class);

  /**
   * @param args
   * @throws InterpreterException
   */
  public static void main(String[] args) throws InterpreterException {

    PropertyConfigurator.configure("src/test/resources/log4j.properties");

    for (int i = 0; i < 100; i++) {
      runTest();
    }

    log.error("started");
    long start = System.currentTimeMillis();
    for (int i = 0; i < TEST_NUM; i++) {
      runTest();
    }
    long end= System.currentTimeMillis();
    log.error("finished " + ((TEST_NUM*1000.0)/(end-start)));
  }

  public static void runTest() throws InterpreterException {
    String ageSumQuery = " GATHER"
        + "  LET peerData = LOAD (\"peerData.xml\" ) "
        + "  LET userFriends =  "
        + "        XPATH(\".//friends/friend\", peerData, TRUE ) "
        + "     IS PRIVATE_MY AS LIST < INTEGER > "
        + "  LET peerAge = IF(LEAF_EXECUTION, XPATH_NOISE(\".//age/value\", peerData, FALSE), XPATH(\".//age/value\", peerData, FALSE)) IS PRIVATE_COND AS INTEGER"
        /*
        + "  LET privacyTests = CheckPrivacy(XPATH (\".//friends/friend\", LOAD (\"peerData.xml\" ), TRUE ), \"PrivateConditionalMy\") "
        + "  LET privacyTest_2 = CheckPrivacy(XPATH_NOISE (\".//age/value\", peerData, FALSE ), \"PublicMy\") "
        + "  LET privacyTest_3 = CHECK_BOOL(IF(1 = 3, 1 = 7, 1.0 = 1.0), TRUE) "
        + "  LET privacyTest_3 = CHECK_BOOL(IF(1 = 1, 1 = 7, 1.0 = 1.0), FALSE) "
        + "  LET privacyTest_4 = CheckPrivacy(peerData, \"PrivateMy\") "
        + "  LET test_results = CREATE_LIST(1 IS PUBLIC_OTHER, 2 IS PUBLIC_OTHER) "
        + "  LET privacyTest_6 = CheckPrivacy(test_results, \"PublicOthers\") "
        + "  LET append_test = APPEND(3, test_results) "
        // + "  LET privacyTest_5 = SHOULD_FAIL(test_results IS PRIVATE_MY) "
        // TODO: How to test this?
        + "  LET privacyTest_7 = CheckPrivacy(test_results, \"PublicMy\") "
        + "  LET append_test_2 = APPEND(peerAge, test_results) "
        + "  LET length_test = LENGTH(test_results) "
        + "  LET fresh_test_results = CREATE_LIST(PURGE_SOURCES(1 IS PUBLIC_OTHER), PURGE_SOURCES(2 IS PUBLIC_OTHER)) "
        + "  LET sum_test = SUM(APPEND(peerAge, fresh_test_results)) "
        + "  LET sum_test_privacy = checkprivacy(sum_test, \"PublicConditionalMy\") "
        + "  LET priv_cond_value = PURGE_SOURCES(10002 IS PRIVATE_COND) "
        + "  LET public_to_private_test = checkprivacy(sum_test + priv_cond_value, \"PublicConditionalMy\") "
         */
         + " FORWARD"
         + " MAX DEPTH 2"
         + " TO"
         + "  userFriends"
         + " REDUCE"
         + "    SUM(APPEND(peerAge, DQL_RESULTS))";
    String sender = "dummySender";

    log.info("Started");
    DQLInterpreter interpreter = new DQLInterpreter(new ExecutorContext(
        "./resources/test/avgAgeTest/2/"));

    PreparedQuery preparedQuery = interpreter.prepareQuery(ageSumQuery, 1, 2);

    InterpreterState state = interpreter.createEmptyState();
    state.mockFunction(new CheckPrivacyLevel());
    state.mockFunction(new ShouldFail());
    state.mockFunction(new CheckBool());
    state.mockFunction(new PurgeSources());

    state = interpreter.runForward(preparedQuery,
        interpreter.runGather(preparedQuery, state));

    // TODO: Returned from other peers values
    state.addFromForward(new IntegerValue(28, new PublicOthers()));
    state.addFromForward(new IntegerValue(58, new PublicOthers()));
    state.addFromForward(new IntegerValue(18, new PublicOthers()));

    state = interpreter.runReduce(preparedQuery, state);

    IntegerValue ageSum = (IntegerValue) state.getReduceResult();

    log.info("Finished sum query\n\n\n");

    log.info("Moving to count query");


    String ageCountQuery = " GATHER"
        + "  LET peerData = LOAD (\"peerData.xml\" ) "
        + "  LET userFriends =  "
        + "        XPATH(\".//friends/friend\", peerData, TRUE ) "
        + "     IS PRIVATE_MY AS LIST < INTEGER > "
        + " FORWARD"
        + " MAX DEPTH 2"
        + " TO"
        + "  userFriends"
        + " REDUCE"
        + "    SUM(DQL_RESULTS) + 1";


    interpreter = new DQLInterpreter(new ExecutorContext(
        "./resources/test/avgAgeTest/2/"));

    preparedQuery = interpreter.prepareQuery(ageCountQuery,
        2, 1);

    state = interpreter.runForward(preparedQuery,
        interpreter.runGather(preparedQuery, interpreter.createEmptyState()));

    // TODO: Returned from other peers values
    state.addFromForward(new IntegerValue(1, new PublicOthers()));
    state.addFromForward(new IntegerValue(1, new PublicOthers()));
    state.addFromForward(new IntegerValue(1, new PublicOthers()));

    state = interpreter.runReduce(preparedQuery, state);

    IntegerValue ageCount= (IntegerValue) state.getReduceResult();

    log.info("Finished count query");

    log.info(ageSum.divNum(ageCount));
  }

}
