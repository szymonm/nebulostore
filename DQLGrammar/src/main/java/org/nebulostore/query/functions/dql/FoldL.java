package org.nebulostore.query.functions.dql;

import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.language.interpreter.datatypes.DQLComplexType;
import org.nebulostore.query.language.interpreter.datatypes.DQLComplexType.DQLComplexTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.LambdaValue;
import org.nebulostore.query.language.interpreter.datatypes.values.ListValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class FoldL extends DQLFunction {

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder()
      .parameter(0, new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLLambda))
      .parameter(2, new DQLComplexType(DQLComplexTypeEnum.DQLList, null))
      .build();

  public FoldL(ExecutorContext context) {
    super("foldl", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws InterpreterException,
      RecognitionException {

    IDQLValue acc = params.get(1);

    for (IDQLValue element : ((ListValue) params.get(2))) {
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
