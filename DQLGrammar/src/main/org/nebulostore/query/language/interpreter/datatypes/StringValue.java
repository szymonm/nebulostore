package org.nebulostore.query.language.interpreter.datatypes;

public class StringValue extends DQLValue {

  private final String value_;

  public StringValue(String value) {
    value_ = value;
  }

  @Override
  public Object toJava() {
    return value_;
  }

}
