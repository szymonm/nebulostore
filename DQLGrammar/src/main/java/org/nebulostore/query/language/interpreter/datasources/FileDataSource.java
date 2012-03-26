package org.nebulostore.query.language.interpreter.datasources;

import java.util.HashMap;
import java.util.Map;

public class FileDataSource extends DQLDataSource {

  static private Map<String, FileDataSource> dataSources_ = new HashMap<String, FileDataSource>();
  private final String fileName_;

  static public FileDataSource getInstance(String fileName) {
    if (!dataSources_.containsKey(fileName)) {
      dataSources_.put(fileName, new FileDataSource(fileName));
    }
    return dataSources_.get(fileName);
  }

  private FileDataSource(String fileName) {
    fileName_ = fileName;
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
    if (other instanceof FileDataSource) {
      return ((FileDataSource) other).fileName_.equals(fileName_);
    }
    return false;
  }

  @Override
  public String toString() {
    return "FileDataSource(" + fileName_ + ")";
  }

}
