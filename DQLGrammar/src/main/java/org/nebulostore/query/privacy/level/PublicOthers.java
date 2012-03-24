package org.nebulostore.query.privacy.level;

import java.util.List;

import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
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
  public String toString() {
    return "PublicOthers";
  }

  @Override
  protected PrivacyLevel freshCopy() {
    return new PublicOthers(dataSources_);
  }

  @Override
  public boolean equals(Object o) {
    // TODO: Czy to jest poprawne
    return o instanceof PublicOthers;
  }

}
