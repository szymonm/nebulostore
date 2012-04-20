package org.nebulostore.query.functions.dql;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.ListValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class Pop extends DQLFunction {

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder()
      .parametersNumber(1).build();

  public Pop(ExecutorContext context) {
    super("Pop", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
  InterpreterException, RecognitionException {
    ListValue l = (ListValue) params.get(0);
    IDQLValue ret = l.get(0);
    l.remove(0);
    return ret;
  }

  @Override
  public String help() {
    // TODO Auto-generated method stub
    return null;
  }



}
