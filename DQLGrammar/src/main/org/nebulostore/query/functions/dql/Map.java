package org.nebulostore.query.functions.dql;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Map extends DQLFunction {

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder().build();

  public Map() {
    super("map", conditions_);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
      InterpreterException, RecognitionException {
    throw new NotImplementedException();
  }

  @Override
  public String help() {
    throw new NotImplementedException();
  }
}
