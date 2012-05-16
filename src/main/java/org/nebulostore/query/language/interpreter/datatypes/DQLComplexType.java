package org.nebulostore.query.language.interpreter.datatypes;

import java.util.List;

public class DQLComplexType extends DQLType {

  private static final long serialVersionUID = 1880308496165825633L;

  public enum DQLComplexTypeEnum {
    DQLList, DQLTuple
  };

  private final DQLComplexTypeEnum type_;
  private final List<DQLType> contentTypes_;

  public DQLComplexType(DQLComplexTypeEnum type, List<DQLType> contentTypes) {
    type_ = type;
    contentTypes_ = contentTypes;
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  public List<DQLType> getComplexTypeContents() {
    return contentTypes_;
  }

  public DQLComplexTypeEnum getType() {
    return type_;
  }

  @Override
  public boolean equals(Object o) {
    return (o != null) && (o instanceof DQLComplexType) &&
        (((DQLComplexType) o).getType() == type_) &&
        (((DQLComplexType) o).getComplexTypeContents().equals(contentTypes_));
  }

  @Override
  public String toString() {
    String ret = "[ ";
    switch (type_) {
    case DQLList:
      ret += "List";
      break;
    case DQLTuple:
      ret += "Tuple";
      break;
    }
    ret += "( ";

    for (DQLType type : contentTypes_) {
      ret += type.toString() + ", ";
    }

    return ret + " ) ]";
  }
}
