package org.nebulostore.query.executor;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.networkmonitor.NetworkContext;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;

public class ExecutorContext {

  private final Map<String, IDQLValue> noiseValues_;
  private final Random random_;
  private final String dataPath_;
  private int localAppKey_;

  public ExecutorContext(String dataPath) {
    dataPath_ = dataPath;
    random_ = new Random(System.currentTimeMillis());
    noiseValues_ = new HashMap<String, IDQLValue>();
  }

  public AppKey getAppKey() {
    return NetworkContext.getInstance().getAppKey();
  }

  public ObjectId getFilesMapObjectId() {
    return new ObjectId(BigInteger.ONE);
  }

  public void setNoiseValue(String key, IDQLValue value) {
    synchronized (noiseValues_) {
      noiseValues_.put(key, value);
    }
  }

  public IDQLValue getNoiseValue(String key) {
    synchronized (noiseValues_) {
      return noiseValues_.get(key);
    }
  }

  public Random getRandom() {
    return random_;
  }

  public void setLocalAppKey(int localAppKey) {
    localAppKey_ = localAppKey;
  }

  public String getDataPath() {
    return dataPath_ + "/" + localAppKey_ + "/";
  }
}
