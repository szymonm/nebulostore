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

public class HintFriendsTest {

  private static Log log = LogFactory.getLog(HintFriendsTest.class);

  /**
   * @param args
   * @throws InterpreterException
   */
  public static void main(String[] args) throws InterpreterException {
    String testQuery = "GATHER "
        + "LET peerData = LOAD (\"peerData.xml\") "
        + "LET peerAdditionalInfo = LOAD (\"peerAdditionalInfo.xml\") "
        + "LET peerBooks = LOAD (\"peerBooks.xml\") "
        + "LET peerSports = LOAD (\"peerSports.xml\") "
        + "LET userFriends = "
        + "      XPATH(\".//friends/friend\", peerData, TRUE ) "
        + "   IS PRIVATE_MY AS LIST < INTEGER > "
        + "LET name = XPATH(\".//name/value\", peerData, FALSE ) "
        + "   IS PUBLIC_MY AS STRING "
        + "LET sports = XPATH(\".//sports/value\", peerSports, FALSE ) "
        + "   IS PRIVATE_COND "
        + "LET obfuscate = XPATH(\".//obfuscate/value\", peerData, FALSE ) "
        + "   IS PUBLIC_MY "
        + "LET obfuscateDifferent = XPATH(\".//obfuscate/value\", peerAdditionalInfo, FALSE ) "
        + "   IS PUBLIC_MY "
        + "LET obfuscateYetDifferent = XPATH(\".//obfuscate/value\", peerBooks, FALSE ) "
        + "   IS PUBLIC_MY "
        + "FORWARD "
        + "MAX DEPTH 2 "
        + "TO "
        + " userFriends "
        + "REDUCE "
        + "   IF(sports*obfuscateDifferent > obfuscateDifferent, APPEND(name, DQL_RESULTS), DQL_RESULTS) ";

    String sender = "dummySender";

    log.info("Started");
    DQLInterpreter interpreter = new DQLInterpreter(new ExecutorContext("./resources/test/query/pajek-40/"));

    PreparedQuery preparedQuery = interpreter.prepareQuery(testQuery,
        2, 1);

    InterpreterState state = interpreter.createEmptyState();
    // state.mockFunction(new FilterMocked());

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
