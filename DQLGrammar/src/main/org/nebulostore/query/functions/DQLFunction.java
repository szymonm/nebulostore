package org.nebulostore.query.functions;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

abstract public class DQLFunction implements IDQLFunction {

  private final String name_;
  private final CallParametersConditions conditions_;

  public DQLFunction(String name, CallParametersConditions conditions) {
    name_ = name.toLowerCase();
    conditions_ = conditions;
  }

  /* (non-Javadoc)
   * @see org.nebulostore.query.functions.IDQLFunction#call(java.util.List)
   */
  @Override
  abstract public IDQLValue call(List<IDQLValue> params)
      throws FunctionCallException, InterpreterException, RecognitionException;

  /* (non-Javadoc)
   * @see org.nebulostore.query.functions.IDQLFunction#getName()
   */
  @Override
  public String getName() {
    return name_;
  }

  protected void checkParams(List<IDQLValue> params)
      throws FunctionCallException {
    conditions_.check(params);
  }
}
