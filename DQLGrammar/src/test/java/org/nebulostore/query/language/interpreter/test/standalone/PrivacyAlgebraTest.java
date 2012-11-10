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
import org.nebulostore.query.language.interpreter.test.functions.CheckPrivacyLevel;
import org.nebulostore.query.language.interpreter.test.functions.ShouldFail;
import org.nebulostore.query.privacy.level.PublicOthers;

public class PrivacyAlgebraTest {

  static Log log = LogFactory.getLog(PrivacyAlgebraTest.class);

  public static void main(String[] args) throws InterpreterException {
    String testQuery = " FORWARD TO"
        + "  userFriends"
        + " GATHER"
        + "  LET secondTest = SHOULD_FAIL(LAMBDA X : ( (1.0 IS PUBLIC_MY AS DOUBLE) + (2.0 IS PRIVATE_MY AS DOUBLE)  IS PUBLIC_MY AS DOUBLE) )"
        + "  LET userFriends =  "
        + "        CheckPrivacy(XPATH (\".//friends/friend\", LOAD (\"peerData.xml\" ), TRUE ), \"PrivateConditionalMy\") "
        + "     IS PRIVATE_MY AS LIST < INTEGER > "
        + "  LET peerAge = XPATH(\".//age/value\", LOAD(\"peerData.xml\"), FALSE)  IS PRIVATE_MY AS INTEGER"
        + "  LET firstTest = (1.0 IS PUBLIC_MY AS DOUBLE) + (2.0 IS PRIVATE_MY AS DOUBLE)  IS PRIVATE_MY AS DOUBLE"

        + " REDUCE"
        + "  CREATE_TUPLE("
        + "    ( SUM(CREATE_LIST(FOLDL(LAMBDA acc, tuple: (acc+GET(tuple, 0) * GET(tuple, 1)), 0, DQL_RESULTS), peerAge) ) )"
        + "                  / (FOLDL(LAMBDA acc, tuple : (acc+GET(tuple, 1)), 0, DQL_RESULTS)   +1)"
        + "    IS PUBLIC_MY AS INTEGER,"
        + "        FOLDL(LAMBDA acc,tuple : (acc+GET(tuple,1)), 0, DQL_RESULTS) + 1 IS PUBLIC_MY AS INTEGER"
        + "  )";
    String sender = "dummySender";

    log.info("Started");
    DQLInterpreter interpreter = new DQLInterpreter(new ExecutorContext(
        "./resources/test/avgAgeTest/1/"));

    PreparedQuery preparedQuery = interpreter.prepareQuery(testQuery, sender,
        1, 2);

    InterpreterState state = interpreter.createEmptyState();
    state.mockFunction(new CheckPrivacyLevel());
    state.mockFunction(new ShouldFail());

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
