package org.nebulostore.query.privacy.level;

import java.util.List;

import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.privacy.PrivacyLevel;

public class PrivateConditionalMy extends PrivacyLevel {

  public PrivateConditionalMy(List<DataSourcesSet> dataSources) {
    super(dataSources);
  }

  public PrivateConditionalMy(DataSourcesSet dataSources) {
    super(dataSources);
  }

  public PrivateConditionalMy() {

  }

  @Override
  public String toString() {
    return "PrivateConditionalMy";
  }

  @Override
  protected PrivacyLevel freshCopy() {
    return new PrivateConditionalMy(dataSources_);
  }

  @Override
  public boolean equals(Object o) {
    // TODO: Czy to jest poprawne
    return o instanceof PrivateConditionalMy;
  }
}
