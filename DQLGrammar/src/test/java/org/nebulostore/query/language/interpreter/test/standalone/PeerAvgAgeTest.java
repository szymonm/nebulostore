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
import org.nebulostore.query.privacy.level.PublicOthers;

public class PeerAvgAgeTest {

  private static Log log = LogFactory.getLog(PeerAvgAgeTest.class);

  /**
   * @param args
   * @throws InterpreterException
   */
  public static void main(String[] args) throws InterpreterException {
    String testQuery = " GATHER"
        + "  LET userFriends =  "
        + "        XPATH (\".//friend/@ref\", LOAD (\"peerData.xml\" ), TRUE ) "
        + "     IS PRIVATE_MY AS LIST < INTEGER > "
        + "  LET peerAge = XPATH(\".//age\", LOAD(\"peerData.xml\"), FALSE)  IS PRIVATE_MY AS INTEGER"
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

    PreparedQuery preparedQuery = interpreter.prepareQuery(testQuery, sender);

    InterpreterState state = interpreter.createEmptyState();
    // state.mockFunction(new FilterMocked());

    state = interpreter.runForward(preparedQuery,
        interpreter.runGather(preparedQuery, state));

    // TODO: Returned from other peers values
    state.addFromForward(new TupleValue(Arrays.asList(new IDQLValue[] {
        new IntegerValue(28, PublicOthers.getInstance()),
        new IntegerValue(300, PublicOthers.getInstance()) }), Arrays
        .asList(new DQLType[] {
            new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger),
            new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger) }),
        PublicOthers.getInstance()));
    state.addFromForward(new TupleValue(Arrays.asList(new IDQLValue[] {
        new IntegerValue(55, PublicOthers.getInstance()),
        new IntegerValue(30, PublicOthers.getInstance()) }), Arrays
        .asList(new DQLType[] {
            new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger),
            new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger) }),
        PublicOthers.getInstance()));
    state.addFromForward(new TupleValue(Arrays.asList(new IDQLValue[] {
        new IntegerValue(18, PublicOthers.getInstance()),
        new IntegerValue(240, PublicOthers.getInstance()) }), Arrays
        .asList(new DQLType[] {
            new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger),
            new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger) }),
        PublicOthers.getInstance()));

    interpreter.runReduce(preparedQuery, state);

    log.info("Finished");

  }
}
