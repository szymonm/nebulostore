package org.nebulostore.query.language.interpreter.datatypes.values.files;

import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType;
import org.nebulostore.query.language.interpreter.datatypes.DQLPrimitiveType.DQLPrimitiveTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.DQLType;
import org.nebulostore.query.language.interpreter.datatypes.values.DQLValue;
import org.nebulostore.query.privacy.PrivacyLevel;

public class FileValue extends DQLValue {

  public FileValue(PrivacyLevel privacyLevel) {
    super(privacyLevel);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Object toJava() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public DQLType getType() {
    return new DQLPrimitiveType(DQLPrimitiveTypeEnum.DQLFile);
  }

  @Override
  public boolean equal(Object o) {
    // TODO Auto-generated method stub
    return false;
  }

}
