package org.nebulostore.query.language.interpreter.datasources;

import java.io.Serializable;

public abstract class DQLDataSource implements Serializable {

  private static final long serialVersionUID = 5818665840550571568L;

  public abstract boolean isIn(DataSourcesSet set);

  @Override
  public abstract boolean equals(Object other);

  @Override
  public abstract String toString();
}
