package org.nebulostore.query.language.interpreter.test.functions;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.values.BooleanValue;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class CheckBool extends DQLFunction {

  private static Log logger_ = LogFactory.getLog(CheckBool.class);

  public CheckBool() {
    super("check_bool", null, null);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
      InterpreterException, RecognitionException {

    BooleanValue toCheck = (BooleanValue) params.get(0);
    BooleanValue otherValue = (BooleanValue) params.get(1);

    if (toCheck.getValue() != otherValue.getValue()) {
      String msg = "Not matching boolean value";
      logger_.fatal(msg);
      throw new InterpreterException(msg);
    }

    return toCheck;
  }

  @Override
  public String help() {
    // TODO Auto-generated method stub
    return null;
  }
}
