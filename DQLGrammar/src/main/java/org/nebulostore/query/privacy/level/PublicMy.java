package org.nebulostore.query.privacy.level;

import java.util.List;

import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.privacy.PrivacyLevel;

public class PublicMy extends PrivacyLevel {

  public PublicMy(List<DataSourcesSet> dataSources) {
    super(dataSources);
  }

  public PublicMy(DataSourcesSet dataSources) {
    super(dataSources);
  }

  public PublicMy() {
  }

  @Override
  public String toString() {
    return "PublicMy";
  }

  @Override
  protected PrivacyLevel freshCopy() {
    return new PublicMy(dataSources_);
  }

  @Override
  public boolean equals(Object o) {
    // TODO: Czy to jest poprawne
    return o instanceof PublicMy;
  }

}
