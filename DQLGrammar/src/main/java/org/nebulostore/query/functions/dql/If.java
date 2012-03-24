package org.nebulostore.query.functions.dql;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.values.BooleanValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class If extends DQLFunction {

  // TODO: finish call parameters
  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder()
      .parameter(0, new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLBoolean))
      .parametersNumber(3).build();

  public If(ExecutorContext context) {
    super("If", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
      InterpreterException, RecognitionException {
    BooleanValue condition = (BooleanValue) params.get(0);
    IDQLValue ret;
    if (condition.getValue()) {
      ret = params.get(1);
    } else {
      ret = params.get(2);
    }
    ret.setPrivacyLevel(condition.getPrivacyLevel().generalize(
        ret.getPrivacyLevel()));
    return ret;
  }

  @Override
  public String help() {
    throw new NotImplementedException();
  }

}
