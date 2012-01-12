package org.nebulostore.query.language.interpreter.datatypes;

import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.language.interpreter.exceptions.TypeException;

public class IntegerValue extends DQLValue {

  private final int value_;

  public IntegerValue(int value) {
    value_ = value;
  }

  public int getValue() {
    return value_;
  }

  @Override
  public IDQLValue add(IDQLValue arg) throws InterpreterException {
    if (arg instanceof IntegerValue) {
      return new IntegerValue(((IntegerValue) arg).value_ + value_);
    } else if (arg instanceof DoubleValue) {
      return new DoubleValue(((DoubleValue) arg).getValue() + value_);
    } else
      throw new TypeException("Unable to add " + this.toString() + " to " +
          arg.toString());
  }

  @Override
  public IDQLValue sub(IDQLValue arg) throws InterpreterException {
    if (arg instanceof IntegerValue) {
      return new IntegerValue(value_ - ((IntegerValue) arg).value_);
    } else if (arg instanceof DoubleValue) {
      return new DoubleValue(value_ - ((DoubleValue) arg).getValue());
    } else
      throw new TypeException("Unable to substract " + this.toString() +
          " to " + arg.toString());
  }

  @Override
  public IDQLValue mult(IDQLValue arg) throws InterpreterException {
    if (arg instanceof IntegerValue) {
      return new IntegerValue(((IntegerValue) arg).value_ * value_);
    } else if (arg instanceof DoubleValue) {
      return new DoubleValue(((DoubleValue) arg).getValue() * value_);
    } else
      throw new TypeException("Unable to multiply " + this.toString() + " to " +
          arg.toString());
  }

  @Override
  public IDQLValue div(IDQLValue arg) throws InterpreterException {
    if (arg instanceof IntegerValue) {
      return new IntegerValue(value_ / ((IntegerValue) arg).value_);
    } else if (arg instanceof DoubleValue) {
      return new DoubleValue(value_ / ((DoubleValue) arg).getValue());
    } else
      throw new TypeException("Unable to substract " + this.toString() +
          " to " + arg.toString());
  }

  @Override
  public IDQLValue mod(IDQLValue arg) throws InterpreterException {
    if (arg instanceof IntegerValue) {
      return new IntegerValue(value_ % ((IntegerValue) arg).value_);
    } else
      throw new TypeException("Unable to substract " + this.toString() +
          " to " + arg.toString());
  }

  @Override
  public IDQLValue numNegation() {
    return new IntegerValue(-value_);
  }

  @Override
  public IDQLValue equals(IDQLValue arg) throws InterpreterException {
    if (arg instanceof IntegerValue)
      return new BooleanValue(value_ == ((IntegerValue) arg).getValue());
    else if (arg instanceof DoubleValue)
      return new BooleanValue(value_ == ((DoubleValue) arg).getValue());
    else
      throw new TypeException("Unable to determine equality between " +
          this.toString() + " and " + arg.toString());
  }

  @Override
  public IDQLValue less(IDQLValue arg) throws InterpreterException {
    if (arg instanceof IntegerValue)
      return new BooleanValue(value_ < ((IntegerValue) arg).getValue());
    else if (arg instanceof DoubleValue)
      return new BooleanValue(value_ < ((DoubleValue) arg).getValue());
    else
      throw new TypeException("Unable to determine less between " +
          this.toString() + " and " + arg.toString());
  }

  @Override
  public IDQLValue lessEquals(IDQLValue arg) throws InterpreterException {
    if (arg instanceof IntegerValue)
      return new BooleanValue(value_ <= ((IntegerValue) arg).getValue());
    else if (arg instanceof DoubleValue)
      return new BooleanValue(value_ <= ((DoubleValue) arg).getValue());
    else
      throw new TypeException("Unable to determine less equals between " +
          this.toString() + " and " + arg.toString());
  }

  @Override
  public IDQLValue greater(IDQLValue arg) throws InterpreterException {
    if (arg instanceof IntegerValue)
      return new BooleanValue(value_ > ((IntegerValue) arg).getValue());
    else if (arg instanceof DoubleValue)
      return new BooleanValue(value_ > ((DoubleValue) arg).getValue());
    else
      throw new TypeException("Unable to determine greater between " +
          this.toString() + " and " + arg.toString());
  }

  @Override
  public IDQLValue greaterEquals(IDQLValue arg) throws InterpreterException {
    if (arg instanceof IntegerValue)
      return new BooleanValue(value_ >= ((IntegerValue) arg).getValue());
    else if (arg instanceof DoubleValue)
      return new BooleanValue(value_ >= ((DoubleValue) arg).getValue());
    else
      throw new TypeException("Unable to determine greater equals between " +
          this.toString() + " and " + arg.toString());
  }

  @Override
  public IDQLValue notEquals(IDQLValue arg) throws InterpreterException {
    return new BooleanValue(!((BooleanValue) equals(arg)).getValue());
  }

  @Override
  public String toString() {
    return "Integer(" + value_ + ")";
  }

  @Override
  public Object toJava() {
    return new Integer(value_);
  }
}
