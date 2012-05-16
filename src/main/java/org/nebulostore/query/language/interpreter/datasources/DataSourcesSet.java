package org.nebulostore.query.language.interpreter.datasources;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DataSourcesSet implements Iterable<DQLDataSource> {
  Set<DQLDataSource> dataSources_;

  public DataSourcesSet() {
    dataSources_ = new HashSet<DQLDataSource>();
  }

  public boolean hasNonEmptyIntersection(DataSourcesSet other) {
    for (DQLDataSource mine : this) {
      if (mine.isIn(other)) {
        return true;
      }
    }
    for (DQLDataSource its : other) {
      if (its.isIn(this)) {
        return true;
      }
    }
    return false;
  }

  public void add(DQLDataSource dataSource) {
    dataSources_.add(dataSource);
  }

  public DataSourcesSet union(DataSourcesSet other) {
    DataSourcesSet ret = new DataSourcesSet();
    for (DQLDataSource mine : this) {
      ret.add(mine);
    }
    for (DQLDataSource its : other) {
      ret.add(its);
    }
    return ret;
  }

  @Override
  public Iterator<DQLDataSource> iterator() {
    return dataSources_.iterator();
  }

  @Override
  public String toString() {
    String ret = "";
    for (DQLDataSource source : this) {
      ret += source.toString() + ", ";
    }
    return ret;
  }

  public DataSourcesSet freshCopy() {
    DataSourcesSet ret = new DataSourcesSet();
    for (DQLDataSource source : this) {
      ret.add(source);
    }
    return ret;
  }

}
