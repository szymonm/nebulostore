package org.nebulostore.query.language.interpreter.datatypes.values;

import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.DQLType;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.language.interpreter.exceptions.TypeException;
import org.nebulostore.query.privacy.PrivacyLevel;

public class BooleanValue extends DQLValue {

  private static final long serialVersionUID = 6062913357271002752L;

  private final boolean value_;

  public boolean getValue() {
    return value_;
  }

  public BooleanValue(boolean value, PrivacyLevel privacyLevel) {
    super(privacyLevel);
    value_ = value;
  }

  public BooleanValue(boolean value) {
    super(null);
    value_ = value;
  }

  @Override
  public IDQLValue equals(IDQLValue arg) throws InterpreterException {
    if (arg instanceof BooleanValue) {
      BooleanValue ret = new BooleanValue(
          value_ == ((BooleanValue) arg).getValue());
      ret.setPrivacyLevel(privacyLevel_.generalize(arg.getPrivacyLevel(), this,
          arg, ret, false));
      return ret;
    } else
      throw new TypeException("Unable to determine equality between " +
          this.toString() + " and " + arg.toString());
  }

  @Override
  public IDQLValue notEquals(IDQLValue arg) throws InterpreterException {
    BooleanValue ret = new BooleanValue(
        !((BooleanValue) equals(arg)).getValue());
    ret.setPrivacyLevel(privacyLevel_.generalize(arg.getPrivacyLevel(), this,
        arg, ret, false));
    return ret;
  }

  @Override
  public String toString() {
    return "Boolean(" + value_ + ") : " + getPrivacyLevel() + "]";
  }

  @Override
  public Object toJava() {
    return value_;
  }

  @Override
  public DQLType getType() {
    return new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLBoolean);
  }

  @Override
  public boolean equal(Object o) {
    return (o instanceof BooleanValue) && ((BooleanValue) o).value_ == value_;
  }
}
