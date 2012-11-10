package org.nebulostore.testing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Test statistics.
 * @author mwalas
 *
 */
public class TestStatistics implements Serializable  {

  private static final long serialVersionUID = -1401461889681405996L;

  private final Map<String, Double> doubleValues_;

  public TestStatistics() {
    doubleValues_ = new HashMap<String, Double>();
  }

  public void setDouble(String key, Double value) {
    doubleValues_.put(key,  value);
  }

  public Double getDouble(String key) {
    return doubleValues_.get(key);
  }

  @Override
  public String toString() {
    return "Double values: " + doubleValues_.toString();
  }
}
