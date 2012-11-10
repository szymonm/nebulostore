package org.nebulostore.query.functions.dql;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.ListValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.level.PrivateMy;

public class CreateList extends DQLFunction {

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder().build();

  public CreateList(ExecutorContext context) {
    super("CREATE_List", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
      InterpreterException, RecognitionException {
    ListValue ret = null;
    for (IDQLValue value : params) {
      if (ret == null) {
        ret = new ListValue(value.getType(), value.getPrivacyLevel());
      }
      ret.add(value);
    }

    if (ret == null) {
      ret = new ListValue(new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLDouble),
          new PrivateMy());
    }

    return ret;
  }

  @Override
  public String help() {
    return "CREATE_LIST : Type* -> [Type]";
  }
}
