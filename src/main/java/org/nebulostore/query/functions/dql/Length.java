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
import org.nebulostore.query.language.interpreter.datatypes.values.TupleValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class Length extends DQLFunction {

  // TODO: Conditions
  private static final CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder().build();

  public Length(ExecutorContext context) {
    super("Length", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
      InterpreterException, RecognitionException {
    IDQLValue listOrTuple = params.get(0);
    if (listOrTuple instanceof ListValue) {
      ListValue list = (ListValue) listOrTuple;
      return new IntegerValue(list.getList().size(), list.getPrivacyLevel()
          .freshCopy());
    } else {
      TupleValue tuple = (TupleValue) listOrTuple;
      return new IntegerValue(tuple.getSize(), tuple.getPrivacyLevel()
          .freshCopy());
    }
  }

  @Override
  public String help() {
    // TODO Auto-generated method stub
    return null;
  }

}
