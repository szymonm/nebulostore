package org.nebulostore.query.language.interpreter.datasources;

public abstract class DQLDataSource {
  public abstract boolean isIn(DataSourcesSet set);

  @Override
  public abstract boolean equals(Object other);
}
