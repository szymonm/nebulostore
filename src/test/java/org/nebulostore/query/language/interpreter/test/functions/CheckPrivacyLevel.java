package org.nebulostore.query.language.interpreter.test.functions;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.StringValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.PrivacyLevel;


public class CheckPrivacyLevel extends DQLFunction {

  private static Log logger_ = LogFactory.getLog(CheckPrivacyLevel.class);

  public CheckPrivacyLevel() {
    super("checkPrivacy", null, null);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
  InterpreterException, RecognitionException {

    PrivacyLevel valuePrivacyLevel = params.get(0).getPrivacyLevel();
    String privacyDesired = ((StringValue) params.get(1)).getValue();

    logger_.info("Checking whether privacy level for value: " + params.get(0) +
        " equals " + privacyDesired);

    if (!privacyDesired.equals(valuePrivacyLevel.toString().split(" ")[0])) {
      logger_.error("Wrong privacy level!");
      throw new InterpreterException("Wrong privacy level for " +
          params.get(0) + " should be " + privacyDesired);
    }

    return params.get(0);
  }

  @Override
  public String help() {
    throw new NotImplementedException();
  }
}