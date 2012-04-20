package org.nebulostore.query.language.interpreter.datatypes.values;

import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.DQLType;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.language.interpreter.exceptions.TypeException;
import org.nebulostore.query.privacy.PrivacyLevel;

public class DoubleValue extends DQLValue {

  private final double value_;

  public DoubleValue(double value, PrivacyLevel privacyLevel) {
    super(privacyLevel);
    value_ = value;
  }

  public DoubleValue(double d) {
    super(null);
    value_ = d;
  }

  public double getValue() {
    return value_;
  }

  @Override
  public IDQLValue addNum(IDQLValue arg) throws InterpreterException {
    DoubleValue ret;
    if (arg instanceof IntegerValue) {
      ret = new DoubleValue(((IntegerValue) arg).getValue() + value_);
    } else if (arg instanceof DoubleValue) {
      ret = new DoubleValue(((DoubleValue) arg).getValue() + value_);
    } else
      throw new TypeException("Unable to add " + this.toString() + " to " +
          arg.toString());

    ret.setPrivacyLevel(privacyLevel_.generalize(arg.getPrivacyLevel(), this,
        arg, ret));
    return ret;
  }

  @Override
  public IDQLValue subNum(IDQLValue arg) throws InterpreterException {
    DoubleValue ret;
    if (arg instanceof IntegerValue) {
      ret = new DoubleValue(value_ - ((IntegerValue) arg).getValue());
    } else if (arg instanceof DoubleValue) {
      ret = new DoubleValue(value_ - ((DoubleValue) arg).getValue());
    } else
      throw new TypeException("Unable to substract " + this.toString() +
          " to " + arg.toString());
    ret.setPrivacyLevel(privacyLevel_.generalize(arg.getPrivacyLevel(), this,
        arg, ret));
    return ret;
  }

  @Override
  public IDQLValue multNum(IDQLValue arg) throws InterpreterException {
    DoubleValue ret;
    if (arg instanceof IntegerValue) {
      ret = new DoubleValue(((IntegerValue) arg).getValue() * value_);
    } else if (arg instanceof DoubleValue) {
      ret = new DoubleValue(((DoubleValue) arg).getValue() * value_);
    } else
      throw new TypeException("Unable to multiply " + this.toString() + " to " +
          arg.toString());
    ret.setPrivacyLevel(privacyLevel_.generalize(arg.getPrivacyLevel(), this,
        arg, ret));
    return ret;
  }

  @Override
  public IDQLValue divNum(IDQLValue arg) throws InterpreterException {
    DoubleValue ret;
    if (arg instanceof IntegerValue) {
      ret = new DoubleValue(value_ / ((IntegerValue) arg).getValue());
    } else if (arg instanceof DoubleValue) {
      ret = new DoubleValue(value_ / ((DoubleValue) arg).getValue());
    } else
      throw new TypeException("Unable to substract " + this.toString() +
          " to " + arg.toString());
    ret.setPrivacyLevel(privacyLevel_.generalize(arg.getPrivacyLevel(), this,
        arg, ret));
    return ret;
  }

  @Override
  public IDQLValue numNegation() throws InterpreterException {
    return new DoubleValue(-value_, privacyLevel_);
  }

  @Override
  public IDQLValue equals(IDQLValue arg) throws InterpreterException {
    BooleanValue ret;
    if (arg instanceof IntegerValue)
      ret = new BooleanValue(value_ == ((IntegerValue) arg).getValue());
    else if (arg instanceof DoubleValue)
      ret = new BooleanValue(value_ == ((DoubleValue) arg).getValue());
    else
      throw new TypeException("Unable to determine equality between " +
          this.toString() + " and " + arg.toString());

    ret.setPrivacyLevel(privacyLevel_.generalize(arg.getPrivacyLevel(), this,
        arg, ret));
    return ret;
  }

  @Override
  public IDQLValue less(IDQLValue arg) throws InterpreterException {
    BooleanValue ret;
    if (arg instanceof IntegerValue)
      ret = new BooleanValue(value_ < ((IntegerValue) arg).getValue());
    else if (arg instanceof DoubleValue)
      ret = new BooleanValue(value_ < ((DoubleValue) arg).getValue());
    else
      throw new TypeException("Unable to determine less between " +
          this.toString() + " and " + arg.toString());
    ret.setPrivacyLevel(privacyLevel_.generalize(arg.getPrivacyLevel(), this,
        arg, ret));
    return ret;
  }

  @Override
  public IDQLValue lessEquals(IDQLValue arg) throws InterpreterException {
    BooleanValue ret;
    if (arg instanceof IntegerValue)
      ret = new BooleanValue(value_ <= ((IntegerValue) arg).getValue());
    else if (arg instanceof DoubleValue)
      ret = new BooleanValue(value_ <= ((DoubleValue) arg).getValue());
    else
      throw new TypeException("Unable to determine less equals between " +
          this.toString() + " and " + arg.toString());
    ret.setPrivacyLevel(privacyLevel_.generalize(arg.getPrivacyLevel(), this,
        arg, ret));
    return ret;
  }

  @Override
  public IDQLValue greater(IDQLValue arg) throws InterpreterException {
    BooleanValue ret;
    if (arg instanceof IntegerValue)
      ret = new BooleanValue(value_ > ((IntegerValue) arg).getValue());
    else if (arg instanceof DoubleValue)
      ret = new BooleanValue(value_ > ((DoubleValue) arg).getValue());
    else
      throw new TypeException("Unable to determine greater between " +
          this.toString() + " and " + arg.toString());
    ret.setPrivacyLevel(privacyLevel_.generalize(arg.getPrivacyLevel(), this,
        arg, ret));
    return ret;
  }

  @Override
  public IDQLValue greaterEquals(IDQLValue arg) throws InterpreterException {
    BooleanValue ret;
    if (arg instanceof IntegerValue)
      ret = new BooleanValue(value_ >= ((IntegerValue) arg).getValue());
    else if (arg instanceof DoubleValue)
      ret = new BooleanValue(value_ >= ((DoubleValue) arg).getValue());
    else
      throw new TypeException("Unable to determine greater equals between " +
          this.toString() + " and " + arg.toString());
    ret.setPrivacyLevel(privacyLevel_.generalize(arg.getPrivacyLevel(), this,
        arg, ret));
    return ret;
  }

  @Override
  public IDQLValue notEquals(IDQLValue arg) throws InterpreterException {
    BooleanValue ret = new BooleanValue(
        !((BooleanValue) equals(arg)).getValue());
    ret.setPrivacyLevel(privacyLevel_.generalize(arg.getPrivacyLevel(), this,
        arg, ret));
    return ret;
  }

  @Override
  public String toString() {
    return "[ Double(" + value_ + ") : " + getPrivacyLevel() + "]";
  }

  @Override
  public Object toJava() {
    return value_;
  }

  @Override
  public DQLType getType() {
    return new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLDouble);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof DoubleValue) && ((DoubleValue) o).value_ == value_;
  }
}
