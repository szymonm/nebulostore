package org.nebulostore.query.privacy.level;

import java.util.List;

import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.PrivacyLevel;

public class PrivateMy extends PrivacyLevel {

  public PrivateMy(List<DataSourcesSet> dataSources) {
    super(dataSources);
  }

  public PrivateMy(DataSourcesSet dataSources_) {
    super(dataSources_);
  }

  public PrivateMy() {
  }

  @Override
  public String toString() {
    return "PrivateMy " + super.toString();
  }

  @Override
  protected PrivacyLevel performGeneralize(PrivacyLevel l, IDQLValue first, IDQLValue second, IDQLValue result)
      throws InterpreterException {
    return this;
  }

  @Override
  protected PrivacyLevel performCompose(PrivacyLevel l, IDQLValue first, IDQLValue second, IDQLValue result)
      throws InterpreterException {
    return this;
  }

  @Override
  public PrivacyLevel freshCopy() {
    return new PrivateMy(dataSources_.freshCopy());
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof PrivateMy;
  }

  @Override
  public boolean canBeSent() {
    return false;
  }

}
