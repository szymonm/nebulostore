package org.nebulostore.query.executor;

import java.math.BigInteger;

import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.broker.NetworkContext;

public class ExecutorContext {

  private final String dataPath_;

  public ExecutorContext(String dataPath) {
    dataPath_ = dataPath;
  }

  public String getDataPath() {
    return dataPath_;
  }

  public AppKey getAppKey() {
    return NetworkContext.getInstance().getAppKey();
  }

  public ObjectId getFilesMapObjectId() {
    return new ObjectId(BigInteger.ONE);
  }
}
