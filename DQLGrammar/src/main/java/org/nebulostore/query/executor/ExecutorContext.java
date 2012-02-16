package org.nebulostore.query.executor;

public class ExecutorContext {

  private final String dataPath_;

  public ExecutorContext(String dataPath) {
    dataPath_ = dataPath;
  }

  public String getDataPath() {
    return dataPath_;
  }

}
