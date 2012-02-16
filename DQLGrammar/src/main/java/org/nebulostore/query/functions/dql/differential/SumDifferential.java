package org.nebulostore.query.functions.dql.differential;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class SumDifferential extends DQLFunction {

  // TODO: call conditions
  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder().build();

  public SumDifferential(ExecutorContext context) {
    super("SUM_DIFFERENTIAL", conditions_, context);
    // TODO Auto-generated constructor stub
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
      InterpreterException, RecognitionException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String help() {
    // TODO Auto-generated method stub
    return null;
  }

}
