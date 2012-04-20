package org.nebulostore.query.functions.dql;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IntegerValue;
import org.nebulostore.query.language.interpreter.datatypes.values.ListValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class Set extends DQLFunction {

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder().parametersNumber(2).build();

  public Set(ExecutorContext context) {
    super("Set", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
  InterpreterException, RecognitionException {

    ListValue l = (ListValue) params.get(0);
    IntegerValue position = (IntegerValue) params.get(1);
    IDQLValue toSet = params.get(2);
    l.set(position.getValue(), toSet);
    return l;
  }

  @Override
  public String help() {
    // TODO Auto-generated method stub
    return null;
  }

}
