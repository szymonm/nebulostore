package org.nebulostore.query.language.interpreter.datatypes.values;

import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.DQLType;
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
    return new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLString);
  }

  public String getValue() {
    return value_;
  }

  @Override
  public String toString() {
    return "[ StringValue(" + value_ + ") : " + getPrivacyLevel() + " ]";
  }

  @Override
  public boolean equals(Object o) {
    // TODO Auto-generated method stub
    return false;
  }
}
