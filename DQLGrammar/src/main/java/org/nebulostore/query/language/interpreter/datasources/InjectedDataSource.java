package org.nebulostore.query.language.interpreter.datasources;

import java.util.HashMap;
import java.util.Map;

/**
 * Data source for injected variables to interpreter execution, such as:
 * DQL_RESULTS, LEAF_EXECUTION.
 * 
 * @author Marcin Walas
 */
public class InjectedDataSource extends DQLDataSource {

  public static String LEAF_EXECUTION = "LEAF_EXECUTION";
  public static String DQL_RESULTS = "DQL_RESULTS";

  static private Map<String, InjectedDataSource> dataSources_ = new HashMap<String, InjectedDataSource>();
  private final String varName_;

  static public InjectedDataSource getInstance(String varName) {
    if (!dataSources_.containsKey(varName)) {
      dataSources_.put(varName, new InjectedDataSource(varName));
    }
    return dataSources_.get(varName);
  }

  private InjectedDataSource(String varName) {
    varName_ = varName;
  }

  @Override
  public boolean isIn(DataSourcesSet set) {
    for (DQLDataSource its : set) {
      if (its.equals(this)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof InjectedDataSource) {
      return ((InjectedDataSource) other).varName_.equals(varName_);
    }
    return false;
  }

}
