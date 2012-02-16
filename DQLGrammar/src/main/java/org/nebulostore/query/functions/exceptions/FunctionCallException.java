package org.nebulostore.query.functions.exceptions;

import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public class FunctionCallException extends InterpreterException {

  private static final long serialVersionUID = 1832110083925426899L;

  public FunctionCallException(Exception e) {
    super(e);
  }

  public FunctionCallException(String e) {
    super(e);
  }

}
