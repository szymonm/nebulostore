package org.nebulostore.query.privacy.level;

import java.util.List;

import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.PrivacyLevel;

public class PublicOthers extends PrivacyLevel {

  private PublicOthers(List<DataSourcesSet> dataSources) {
    super(dataSources);
  }

  public PublicOthers(DataSourcesSet dataSources) {
    super(dataSources);
  }

  public PublicOthers() {
  }

  @Override
  public PrivacyLevel freshCopy() {
    return new PublicOthers(dataSources_.freshCopy());
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
  public boolean equals(Object o) {
    return o instanceof PublicOthers;
  }

  @Override
  public String toString() {
    return "PublicOthers " + super.toString();
  }

}
