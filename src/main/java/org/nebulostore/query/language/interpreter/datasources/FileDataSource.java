package org.nebulostore.query.language.interpreter.datasources;

import java.util.HashMap;
import java.util.Map;

public class FileDataSource extends DQLDataSource {

  static private Map<String, FileDataSource> dataSources_ = new HashMap<String, FileDataSource>();
  private final String fileName_;
  private final String queryPath_;

  static public FileDataSource getInstance(String fileName, String queryPath) {
    if (!dataSources_.containsKey(fileName + queryPath)) {
      dataSources_.put(fileName + queryPath, new FileDataSource(fileName,
          queryPath));
    }
    return dataSources_.get(fileName + queryPath);
  }

  private FileDataSource(String fileName, String queryPath) {
    fileName_ = fileName;
    queryPath_ = queryPath;
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
    if (other != null && other instanceof FileDataSource) {
      FileDataSource fileSource = (FileDataSource) other;
      if (fileSource.fileName_.equals(fileName_)) {
        if (fileSource.queryPath_.startsWith(queryPath_) ||
            queryPath_.startsWith(fileSource.queryPath_))
          return true;
        return false;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "FileDataSource(" + fileName_ + " : " + queryPath_ + ")";
  }

}
