package org.nebulostore.query.functions.dql;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class If extends DQLFunction {

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder().build();

  public If() {
    super("If", conditions_);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
      InterpreterException, RecognitionException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String help() {
    throw new NotImplementedException();
  }

}
