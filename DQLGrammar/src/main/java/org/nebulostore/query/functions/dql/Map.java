package org.nebulostore.query.functions.dql;

import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.LambdaValue;
import org.nebulostore.query.language.interpreter.datatypes.values.ListValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Map extends DQLFunction {

  // TODO: call conditions
  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder().build();

  public Map(ExecutorContext context) {
    super("map", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
      InterpreterException, RecognitionException {
    ListValue ret = null;
    for (IDQLValue element : ((ListValue) params.get(1))) {
      List<IDQLValue> lambdaParams = new LinkedList<IDQLValue>();
      lambdaParams.add(element);
      IDQLValue toAdd = ((LambdaValue) params.get(0)).evaluate(lambdaParams);
      if (ret == null) {
        ret = new ListValue(toAdd.getType(), toAdd.getPrivacyLevel());
      }
      ret.add(toAdd);
    }

    return ret;
  }

  @Override
  public String help() {
    throw new NotImplementedException();
  }
}
