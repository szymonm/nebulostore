package org.nebulostore.query.functions.ql.xml;

import java.util.List;

import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.DQLValue.DQLType;
import org.nebulostore.query.language.interpreter.datatypes.IDQLValue;

public class Load extends DQLFunction {

  private static CallParametersConditions conditions_ = CallParametersConditions
      .newBuilder().parameter(0, DQLType.DQLString).build();

  public Load() {
    super("Load", conditions_);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException {
    checkParams(params);
    return null;
  }
}
