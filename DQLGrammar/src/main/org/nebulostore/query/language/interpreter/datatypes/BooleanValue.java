package org.nebulostore.query.language.interpreter.datatypes;

import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.language.interpreter.exceptions.TypeException;
import org.nebulostore.query.privacy.PrivacyLevel;

public class BooleanValue extends DQLValue {
  private final boolean value_;

  public boolean getValue() {
    return value_;
  }

  public BooleanValue(boolean value, PrivacyLevel privacyLevel) {
    super(privacyLevel);
    value_ = value;
  }

  @Override
  public IDQLValue equals(IDQLValue arg) throws InterpreterException {
    if (arg instanceof BooleanValue)
      return new BooleanValue(value_ == ((BooleanValue) arg).getValue(),
          privacyLevel_.generalize(arg.getPrivacyLevel()));
    else
      throw new TypeException("Unable to determine equality between " +
          this.toString() + " and " + arg.toString());
  }

  @Override
  public IDQLValue notEquals(IDQLValue arg) throws InterpreterException {
    return new BooleanValue(!((BooleanValue) equals(arg)).getValue(),
        privacyLevel_.generalize(arg.getPrivacyLevel()));
  }

  @Override
  public String toString() {
    return "Double(" + value_ + ")";
  }

  @Override
  public Object toJava() {
    return value_;
  }

  @Override
  public DQLType getType() {
    return DQLType.DQLBoolean;
  }
}
