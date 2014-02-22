package org.nebulostore.persistence;

/**
 * Interface for any persistent key/value store. All implementations should be thread-safe.
 *
 * @author Bolek Kulbabinski
 */
public interface KeyValueStore {
  void putString(String key, String value) throws StoreException;

  String getString(String key) throws StoreException;

  void putBytes(String key, byte[] data) throws StoreException;

  byte[] getBytes(String key) throws StoreException;

  void delete(String key) throws StoreException;
}
