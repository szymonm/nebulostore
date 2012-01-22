package org.nebulostore.query.language.interpreter.datatypes;

import org.nebulostore.query.privacy.PrivacyLevel;

public class StringValue extends DQLValue {

  private final String value_;

  public StringValue(String value, PrivacyLevel privacyLevel) {
    super(privacyLevel);
    value_ = value;
  }

  @Override
  public Object toJava() {
    return value_;
  }

  @Override
  public DQLType getType() {
    return DQLType.DQLString;
  }

  public String getValue() {
    return value_;
  }

}
