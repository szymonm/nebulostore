package org.nebulostore.query.functions.dql;

import java.util.List;

import org.nebulostore.query.functions.CallParametersConditions;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.IDQLValue;

public class Filter extends DQLFunction {
  private static CallParametersConditions conditions_;

  public Filter(String name) {
    super(name, conditions_);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException {
    // TODO Auto-generated method stub
    return null;
  }

}
