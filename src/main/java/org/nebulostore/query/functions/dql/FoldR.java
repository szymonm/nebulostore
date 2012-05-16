package org.nebulostore.query.functions.dql;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.lang.NotImplementedException;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.LambdaValue;
import org.nebulostore.query.language.interpreter.datatypes.values.ListValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class FoldR extends DQLFunction {
  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder().build();

  public FoldR(ExecutorContext context) {
    super("foldr", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws InterpreterException,
  RecognitionException {
    checkParams(params);
    IDQLValue acc = params.get(1);

    List<IDQLValue> tmpList = new LinkedList<IDQLValue>(
        ((ListValue) params.get(2)).getList());
    Collections.reverse(tmpList);

    for (IDQLValue element : tmpList) {
      List<IDQLValue> lambdaParams = new LinkedList<IDQLValue>();
      lambdaParams.add(acc);
      lambdaParams.add(element);
      acc = ((LambdaValue) params.get(0)).evaluate(lambdaParams);
    }

    return acc;
  }

  @Override
  public String help() {
    throw new NotImplementedException();
  }

}
