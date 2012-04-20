package org.nebulostore.query.functions.dql;

import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.executor.ExecutorContext;
import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.DQLType;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.TupleValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.PrivacyLevel;
import org.nebulostore.query.privacy.level.PublicMy;

public class CreateTuple extends DQLFunction {

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder().build();

  public CreateTuple(ExecutorContext context) {
    super("CREATE_TUPLE", conditions_, context);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
  InterpreterException, RecognitionException {
    LinkedList<DQLType> types = new LinkedList<DQLType>();
    PrivacyLevel privacyLevel = new PublicMy();
    for (IDQLValue value : params) {
      types.add(value.getType());
      privacyLevel = privacyLevel.compose(value.getPrivacyLevel(), null, null,
          null);
    }
    return new TupleValue(params, types, privacyLevel);
  }

  @Override
  public String help() {
    return "CREATE_TUPLE : Type1, Type2, ... -> (Type1, Type2, ...)";
  }
}
