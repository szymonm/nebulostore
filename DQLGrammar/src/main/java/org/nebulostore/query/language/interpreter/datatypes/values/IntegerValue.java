package org.nebulostore.query.language.interpreter.datatypes.values;

import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.DQLType;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.language.interpreter.exceptions.TypeException;
import org.nebulostore.query.privacy.PrivacyLevel;

public class IntegerValue extends DQLValue {

  private final int value_;

  public IntegerValue(int value, PrivacyLevel privacyLevel) {
    super(privacyLevel);
    value_ = value;
  }

  public IntegerValue(int value) {
    super(null);
    value_ = value;
  }

  public int getValue() {
    return value_;
  }

  @Override
  public IDQLValue addNum(IDQLValue arg) throws InterpreterException {
    DQLValue ret;
    if (arg instanceof IntegerValue) {
      ret = new IntegerValue(((IntegerValue) arg).value_ + value_);
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
    DQLValue ret;
    if (arg instanceof IntegerValue) {
      ret = new IntegerValue(value_ - ((IntegerValue) arg).value_);
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
    DQLValue ret;
    if (arg instanceof IntegerValue) {
      ret = new IntegerValue(((IntegerValue) arg).value_ * value_);
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
    DQLValue ret;
    if (arg instanceof IntegerValue) {
      ret = new IntegerValue(value_ / ((IntegerValue) arg).value_);
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
  public IDQLValue modNum(IDQLValue arg) throws InterpreterException {
    IntegerValue ret;
    if (arg instanceof IntegerValue) {
      ret = new IntegerValue(value_ % ((IntegerValue) arg).value_);
    } else
      throw new TypeException("Unable to substract " + this.toString() +
          " to " + arg.toString());
    ret.setPrivacyLevel(privacyLevel_.generalize(arg.getPrivacyLevel(), this,
        arg, ret));
    return ret;
  }

  @Override
  public IDQLValue numNegation() {
    return new IntegerValue(-value_, privacyLevel_);
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
    return "[ Integer(" + value_ + ") : " + getPrivacyLevel() + " ]";
  }

  @Override
  public Object toJava() {
    return new Integer(value_);
  }

  @Override
  public DQLType getType() {
    return new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLInteger);
  }

  @Override
  public boolean equal(Object o) {
    return (o instanceof IntegerValue) && ((IntegerValue) o).value_ == value_;
  }
}
