package org.nebulostore.query.language.interpreter.datasources;

public class ConstantDataSource extends DQLDataSource {

  private static ConstantDataSource instance_;

  public static ConstantDataSource getInstance() {
    if (instance_ == null) {
      instance_ = new ConstantDataSource();
    }
    return instance_;
  }

  @Override
  public boolean isIn(DataSourcesSet set) {
    // Constant data source is in every possible set
    return true;
  }

  public DataSourcesSet toDataSourcesSet() {
    DataSourcesSet ret = new DataSourcesSet();
    ret.add(this);
    return ret;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof ConstantDataSource) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "ConstantDataSource";
  }

}
