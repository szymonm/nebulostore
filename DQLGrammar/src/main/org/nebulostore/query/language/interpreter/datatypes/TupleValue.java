package org.nebulostore.query.language.interpreter.datatypes;

import java.util.List;

public class TupleValue extends DQLValue {

  private List<IDQLValue> value_;
  private List<IDQLValue> types_;
  private int size_;

  @Override
  public Object toJava() {
    return JavaValuesGlue.fromDQL(value_);
  }

}
