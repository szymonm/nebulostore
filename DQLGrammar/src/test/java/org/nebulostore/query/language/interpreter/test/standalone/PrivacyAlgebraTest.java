package org.nebulostore.query.language.interpreter.test.standalone;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.DQLInterpreter;
import org.nebulostore.query.language.interpreter.InterpreterState;
import org.nebulostore.query.language.interpreter.PreparedQuery;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.DQLType;
import org.nebulostore.query.language.interpreter.datatypes.values.DoubleValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IntegerValue;
import org.nebulostore.query.language.interpreter.datatypes.values.LambdaValue;
import org.nebulostore.query.language.interpreter.datatypes.values.StringValue;
import org.nebulostore.query.language.interpreter.datatypes.values.TupleValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.PrivacyLevel;
import org.nebulostore.query.privacy.level.PrivateMy;
import org.nebulostore.query.privacy.level.PublicOthers;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PrivacyAlgebraTest {

  private static Log log = LogFactory.getLog(PrivacyAlgebraTest.class);

  private static class CheckPrivacyLevel extends DQLFunction {

    public CheckPrivacyLevel() {
      super("checkPrivacy", null, null);
    }

    @Override
    public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
        InterpreterException, RecognitionException {

      PrivacyLevel valuePrivacyLevel = params.get(0).getPrivacyLevel();
      String privacyDesired = ((StringValue) params.get(1)).getValue();

      log.info("Checking whether privacy level for value: " + params.get(0) +
          " equals " + privacyDesired);

      if (!privacyDesired.equals(valuePrivacyLevel.toString())) {
        log.error("Wrong privacy level!");
        throw new InterpreterException("Wrong privacy level for " +
            params.get(0) + " should be " + privacyDesired);
      }

      return params.get(0);
    }

    @Override
    public String help() {
      throw new NotImplementedException();
    }
  }

  private static class ShouldFail extends DQLFunction {

    public ShouldFail() {
      super("should_fail", null, null);
    }

    @Override
    public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
        InterpreterException, RecognitionException {

      boolean raised = false;

      List<IDQLValue> lambdaParams = new LinkedList<IDQLValue>();
      lambdaParams.add(new DoubleValue(1.0, PrivateMy.getInstance()));

      try {
        ((LambdaValue) params.get(0)).evaluate(lambdaParams);
      } catch (Throwable t) {
        log.info("OK: Exception caugth: " + t);
        raised = true;
      }

      if (!raised) {
        throw new InterpreterException("Lambda should raise an exception!");
      }
      return new DoubleValue(1.0, PrivateMy.getInstance());
    }

    @Override
    public String help() {
      throw new NotImplementedException();
    }
  }

  public static void main(String[] args) throws InterpreterException {
    String testQuery = " GATHER"
        + "  LET secondTest = SHOULD_FAIL(LAMBDA X : ( (1.0 IS PUBLIC_MY AS DOUBLE) + (2.0 IS PRIVATE_MY AS DOUBLE)  IS PUBLIC_MY AS DOUBLE) )"
        + "  LET userFriends =  "
        + "        CheckPrivacy(XPATH (\".//friend/@ref\", LOAD (\"peerData.xml\" ), TRUE ), \"PrivateMy\") "
        + "     IS PRIVATE_MY AS LIST < INTEGER > "
        + "  LET peerAge = XPATH(\".//age\", LOAD(\"peerData.xml\"), FALSE)  IS PRIVATE_MY AS INTEGER"
        + "  LET firstTest = (1.0 IS PUBLIC_MY AS DOUBLE) + (2.0 IS PRIVATE_MY AS DOUBLE)  IS PRIVATE_MY AS DOUBLE"
        + " FORWARD"
        + " MAX DEPTH 2"
        + " TO"
        + "  userFriends"
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

    PreparedQuery preparedQuery = interpreter.prepareQuery(testQuery, sender);

    InterpreterState state = interpreter.createEmptyState();
    state.mockFunction(new CheckPrivacyLevel());
    state.mockFunction(new ShouldFail());

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
