package org.nebulostore.query.language.interpreter.datatypes;

import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.language.interpreter.exceptions.TypeException;

public class BooleanValue extends DQLValue {
  private final boolean value_;

  public boolean getValue() {
    return value_;
  }

  public BooleanValue(boolean value) {
    value_ = value;
  }

  @Override
  public IDQLValue equals(IDQLValue arg) throws InterpreterException {
    if (arg instanceof BooleanValue)
      return new BooleanValue(value_ == ((BooleanValue) arg).getValue());
    else
      throw new TypeException("Unable to determine equality between " +
          this.toString() + " and " + arg.toString());
  }

  @Override
  public IDQLValue notEquals(IDQLValue arg) throws InterpreterException {
    return new BooleanValue(!((BooleanValue) equals(arg)).getValue());
  }

  @Override
  public String toString() {
    return "Double(" + value_ + ")";
  }
}
