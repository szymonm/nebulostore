package org.nebulostore.query.language.interpreter.test.functions;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nebulostore.query.functions.DQLFunction;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class PurgeSources extends DQLFunction {

  private static Log logger_ = LogFactory.getLog(PurgeSources.class);

  public PurgeSources() {
    super("purge_sources", null, null);
  }

  @Override
  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
      InterpreterException, RecognitionException {

    IDQLValue arg = params.get(0);
    logger_.info("Purging sources for: " + arg);
    arg.setToHigherOrEqualsPrivacyLevel(arg.getPrivacyLevel().purgeSources());
    logger_.info("Sources purged for: " + arg);
    return arg;
  }

  @Override
  public String help() {
    // TODO Auto-generated method stub
    return null;
  }
}
