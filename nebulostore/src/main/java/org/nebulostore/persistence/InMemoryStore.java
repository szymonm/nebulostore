package org.nebulostore.persistence;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Charsets;

/**
 * Non-persistent storage.
 *
 * @author Bolek Kulbabinski
 */
public class InMemoryStore implements KeyValueStore {

  private final Map<String, byte[]> map_;

  public InMemoryStore() {
    map_ = new HashMap<>();
  }

  @Override
  public synchronized void putString(String key, String value) {
    putBytes(key, value.getBytes(Charsets.UTF_8));
  }

  @Override
  public synchronized String getString(String key) throws StoreException {
    return new String(getBytes(key), Charsets.UTF_8);
  }

  @Override
  public synchronized void putBytes(String key, byte[] data) {
    map_.put(key, data);
  }

  @Override
  public synchronized byte[] getBytes(String key) throws StoreException {
    if (!map_.containsKey(key)) {
      throw new StoreException("No such key");
    }
    return map_.get(key);
  }

  @Override
  public synchronized void delete(String key) {
    map_.remove(key);
  }
}
