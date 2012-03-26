package org.nebulostore.query.privacy.level;

import java.util.List;

import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.privacy.PrivacyLevel;

public class PublicConditionalMy extends PrivacyLevel {

  public PublicConditionalMy() {

  }

  public PublicConditionalMy(List<DataSourcesSet> dataSources) {
    super(dataSources);
  }

  public PublicConditionalMy(DataSourcesSet dataSources) {
    super(dataSources);
  }

  @Override
  public PrivacyLevel freshCopy() {
    return new PublicConditionalMy(dataSources_);
  }

  @Override
  public boolean equals(Object o) {
    // TODO: Czy to jest poprawne
    return o instanceof PublicConditionalMy;
  }

  @Override
  public String toString() {
    return "PublicConditionalMy " + super.toString();
  }
}
