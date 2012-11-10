package org.nebulostore.query.functions.dql;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.FunctionsLibrary;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class Push extends DQLFunction {

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder()
      // .parameter(1, new DQLComplexType(DQLComplexTypeEnum.DQLList,
      // contentTypes) -- TODO: Jakiś wildcard na typy powinien być
      .parametersNumber(2).build();

  public Push(ExecutorContext context) {
    super("PUSH", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
  InterpreterException, RecognitionException {
    return FunctionsLibrary.getInstance(getContext()).getFunction("APPEND").call(params);
  }

  @Override
  public String help() {
    return null;
  }

}
