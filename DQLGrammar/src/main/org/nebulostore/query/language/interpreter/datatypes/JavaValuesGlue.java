package org.nebulostore.query.language.interpreter.datatypes;

import java.util.LinkedList;
import java.util.List;

public class JavaValuesGlue {

  public static List<Object> fromDQL(List<IDQLValue> params) {
    List<Object> ret = new LinkedList<Object>();
    for (IDQLValue value : params) {
      ret.add(fromDQL(value));
    }
    return ret;
  }

  private static Object fromDQL(IDQLValue value) {
    return value.toJava();
  }

  public static IDQLValue toDQL(Object value) {

    return null;
  }

  public static IDQLValue fromJava(Object value) {
    return toDQL(value);
  }

}
