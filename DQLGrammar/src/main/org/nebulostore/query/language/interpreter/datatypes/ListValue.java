package org.nebulostore.query.language.interpreter.datatypes;

import java.util.List;

public class ListValue extends DQLValue {

  private List<IDQLValue> value_;
  private int size_;
  private DQLType type_;

  @Override
  public Object toJava() {
    return JavaValuesGlue.fromDQL(value_);
  }
}
