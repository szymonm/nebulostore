package org.nebulostore.query.language.interpreter.datatypes;

import java.io.Serializable;

public abstract class DQLType implements Serializable {

  private static final long serialVersionUID = -6743891887881807566L;

  abstract public boolean isPrimitive();
}
