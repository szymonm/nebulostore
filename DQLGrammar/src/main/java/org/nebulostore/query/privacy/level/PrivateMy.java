package org.nebulostore.query.privacy.level;

import java.util.List;

import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
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
  public PrivacyLevel freshCopy() {
    return new PrivateMy(dataSources_);
  }

  @Override
  public boolean equals(Object o) {
    // TODO: Czy to jest poprawne
    return o instanceof PrivateMy;
  }
}
