package org.nebulostore.query.language.interpreter.test.functions;

import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.values.DoubleValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.LambdaValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.level.PrivateMy;

public class ShouldFail extends DQLFunction {

  private static Log logger_ = LogFactory.getLog(ShouldFail.class);

  public ShouldFail() {
    super("should_fail", null, null);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
  InterpreterException, RecognitionException {

    boolean raised = false;

    List<IDQLValue> lambdaParams = new LinkedList<IDQLValue>();
    lambdaParams.add(new DoubleValue(1.0, new PrivateMy()));

    try {
      ((LambdaValue) params.get(0)).evaluate(lambdaParams);
    } catch (Throwable t) {
      logger_.info("OK: Exception caugth: " + t);
      raised = true;
    }

    if (!raised) {
      throw new InterpreterException("Lambda should raise an exception!");
    }
    return new DoubleValue(1.0, new PrivateMy());
  }

  @Override
  public String help() {
    throw new NotImplementedException();
  }
}