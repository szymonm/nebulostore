package org.nebulostore.query.language.interpreter.test.standalone;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.language.interpreter.DQLInterpreter;
import org.nebulostore.query.language.interpreter.InterpreterState;
import org.nebulostore.query.language.interpreter.PreparedQuery;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.DQLType;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IntegerValue;
import org.nebulostore.query.language.interpreter.datatypes.values.TupleValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.language.interpreter.test.functions.CheckBool;
import org.nebulostore.query.language.interpreter.test.functions.CheckPrivacyLevel;
import org.nebulostore.query.language.interpreter.test.functions.ShouldFail;
import org.nebulostore.query.privacy.level.PublicOthers;

public class AuditTest {

  private static Log log = LogFactory.getLog(AuditTest.class);

  /**
   * @param args
   * @throws InterpreterException
   */
  public static void main(String[] args) throws InterpreterException {
    String testQuery = " GATHER"
        + "  LET peerData = LOAD (\"peerData.xml\" ) "
        + "  LET userFriends =  "
        + "        XPATH(\".//friends/friend\", peerData, TRUE ) "
        + "     IS PRIVATE_MY AS LIST < INTEGER > "
        + "  LET peerAge = IF(LEAF_EXECUTION, XPATH_NOISE(\".//age/value\", peerData, FALSE), XPATH(\".//age/value\", peerData, FALSE)) IS PRIVATE_COND AS INTEGER"
        + "  LET privacyTests = CheckPrivacy(XPATH (\".//friends/friend\", LOAD (\"peerData.xml\" ), TRUE ), \"PrivateConditionalMy\") "
        + "  LET privacyTest_2 = CheckPrivacy(XPATH_NOISE (\".//age/value\", peerData, FALSE ), \"PublicMy\") "
        + "  LET privacyTest_3 = CHECK_BOOL(IF(1 = 3, 1 = 7, 1.0 = 1.0), TRUE) "
        + "  LET privacyTest_3 = CHECK_BOOL(IF(1 = 1, 1 = 7, 1.0 = 1.0), FALSE) "
        + "  LET privacyTest_4 = CheckPrivacy(peerData, \"PrivateMy\") "
        + "  LET test_results = CREATE_LIST(1 IS PUBLIC_OTHER, 2 IS PUBLIC_OTHER) "
        // + "  LET privacyTest_5 = SHOULD_FAIL(test_results IS PRIVATE_MY) "
        // TODO: How to test this?
        + "  LET privacyTest_6 = CheckPrivacy(test_results, \"PublicOthers\") "
        + "  LET privacyTest_7 = SHOULD_FAIL( ) "
        + " FORWARD"
        + " MAX DEPTH 2"
        + " TO"
        + "  userFriends"
        + " REDUCE"
        + "  CREATE_TUPLE("
        + "    (FOLDL(LAMBDA acc, tuple: (acc+GET(tuple, 0) * GET(tuple, 1)), 0, DQL_RESULTS) + peerAge)"
        + "                  / (FOLDL(LAMBDA acc, tuple : (acc+GET(tuple, 1)), 0, DQL_RESULTS)   +1)"
        + "    IS PUBLIC_MY AS INTEGER,"
        + "        FOLDL(LAMBDA acc,tuple : (acc+GET(tuple,1)), 0, DQL_RESULTS) + 1 IS PUBLIC_MY AS INTEGER"
        + "  )";
    String sender = "dummySender";

    log.info("Started");
    DQLInterpreter interpreter = new DQLInterpreter(new ExecutorContext(
        "./resources/test/avgAgeTest/2/"));

    PreparedQuery preparedQuery = interpreter.prepareQuery(testQuery, sender,
        2, 1);

    InterpreterState state = interpreter.createEmptyState();
    state.mockFunction(new CheckPrivacyLevel());
    state.mockFunction(new ShouldFail());
    state.mockFunction(new CheckBool());

    state = interpreter.runForward(preparedQuery,
        interpreter.runGather(preparedQuery, state));

    // TODO: Returned from other peers values
    state.addFromForward(new TupleValue(Arrays.asList(new IDQLValue[] {
        new IntegerValue(28, new PublicOthers()),
        new IntegerValue(300, new PublicOthers()) }), Arrays
        .asList(new DQLType[] {
            new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger),
            new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger) }),
        new PublicOthers()));
    state.addFromForward(new TupleValue(Arrays.asList(new IDQLValue[] {
        new IntegerValue(55, new PublicOthers()),
        new IntegerValue(30, new PublicOthers()) }), Arrays
        .asList(new DQLType[] {
            new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger),
            new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger) }),
        new PublicOthers()));
    state.addFromForward(new TupleValue(Arrays.asList(new IDQLValue[] {
        new IntegerValue(18, new PublicOthers()),
        new IntegerValue(240, new PublicOthers()) }), Arrays
        .asList(new DQLType[] {
            new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger),
            new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger) }),
        new PublicOthers()));

    interpreter.runReduce(preparedQuery, state);

    log.info("Finished");

  }

}
