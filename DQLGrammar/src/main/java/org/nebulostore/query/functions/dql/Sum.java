package org.nebulostore.query.functions.dql;

import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.FunctionsLibrary;
import org.nebulostore.query.functions.InlineExecutor;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.DQLComplexType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.values.DoubleValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IntegerValue;
import org.nebulostore.query.language.interpreter.datatypes.values.LambdaValue;
import org.nebulostore.query.language.interpreter.datatypes.values.ListValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.level.PublicMy;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Sum extends DQLFunction {

  // TODO: call conditions
  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder().build();

  public Sum(ExecutorContext context) {
    super("sum", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
      InterpreterException, RecognitionException {
    List<IDQLValue> foldParams = new LinkedList<IDQLValue>();
    foldParams.add(new LambdaValue("LAMBDA acc, item : (acc + item)",
        FunctionsLibrary.getInstance(getContext()).getFunctions()));

    IDQLValue startValue = null;
    switch (((DQLPrimitiveType) ((DQLComplexType) ((ListValue) params.get(0))
        .getType()).getComplexTypeContents().get(0)).getTypeEnum()) {
    case DQLDouble:
      startValue = new DoubleValue(0.0, PublicMy.getInstance());
      break;
    case DQLInteger:
      startValue = new IntegerValue(0, PublicMy.getInstance());
      break;
    }
    foldParams.add(startValue);
    foldParams.add(params.get(0));
    return InlineExecutor.getInstance(getContext())
        .execute("foldl", foldParams);
  }

  @Override
  public String help() {
    throw new NotImplementedException();
  }

}
