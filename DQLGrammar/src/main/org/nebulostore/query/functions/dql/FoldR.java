package org.nebulostore.query.functions.dql;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.language.interpreter.datatypes.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.LambdaValue;
import org.nebulostore.query.language.interpreter.datatypes.ListValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class FoldR extends DQLFunction {
  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder().build();

  public FoldR() {
    super("foldr", conditions_);
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
