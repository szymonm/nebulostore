package org.nebulostore.query.functions;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.nebulostore.query.functions.exceptions.FunctionCallException;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public interface IDQLFunction {

  public IDQLValue call(List<IDQLValue> params) throws FunctionCallException,
      InterpreterException, RecognitionException;

  public String getName();

  public String help();
}