package org.nebulostore.query.language.interpreter.datatypes;

public class DQLPrimitiveType extends DQLType {
  public enum DQLPrimitiveTypeEnum {
    DQLInteger, DQLDouble, DQLString, DQLLambda, DQLBoolean, DQLFile
  }

  private final DQLPrimitiveTypeEnum type_;

  public DQLPrimitiveType(DQLPrimitiveTypeEnum type) {
    type_ = type;
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  public DQLPrimitiveTypeEnum getTypeEnum() {
    return type_;
  }

  @Override
  public boolean equals(Object o) {
    return (o != null) && (o instanceof DQLPrimitiveType) &&
        (((DQLPrimitiveType) o).getTypeEnum() == type_);
  }

  @Override
  public String toString() {
    String ret = "[ ";
    switch (type_) {
    case DQLBoolean:
      ret += "Boolean";
      break;
    case DQLDouble:
      ret += "Double";
      break;
    case DQLFile:
      ret += "File";
      break;
    case DQLInteger:
      ret += "Integer";
      break;
    case DQLLambda:
      ret += "Lambda";
      break;
    case DQLString:
      ret += "String";
      break;
    }

    return ret + " ]";
  }
}
