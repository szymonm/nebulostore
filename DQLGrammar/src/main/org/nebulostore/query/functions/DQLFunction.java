package org.nebulostore.query.functions;

import java.util.List;

import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.IDQLValue;

abstract public class DQLFunction {

  private final String name_;
  private final CallParametersConditions conditions_;

  public DQLFunction(String name, CallParametersConditions conditions) {
    name_ = name.toLowerCase();
    conditions_ = conditions;
  }

  abstract public IDQLValue call(List<IDQLValue> params)
      throws FunctionCallException;

  public String getName() {
    return name_;
  }

  protected void checkParams(List<IDQLValue> params)
      throws FunctionCallException {
    conditions_.check(params);
  }
}
