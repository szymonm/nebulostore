package org.nebulostore.query.functions.dql;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CreateList extends DQLFunction {

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder().build();

  public CreateList() {
    super("CREATE_List", conditions_);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
      InterpreterException, RecognitionException {
    throw new NotImplementedException();
  }

  @Override
  public String help() {
    return "CREATE_LIST : Type* -> [Type]";
  }
}
