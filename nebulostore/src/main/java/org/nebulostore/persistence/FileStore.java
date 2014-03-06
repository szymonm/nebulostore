package org.nebulostore.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * Store data as files in a given directory. Key is used as the file name and value is stored as the
 * file contents.
 *
 * @author Bolek Kulbabinski
 *
 */
public class FileStore implements KeyValueStore {

  private final String rootDir_;
  private final Map<String, Lock> locksMap_;
  private final Map<String, Integer> locksUsersCount_;


  public FileStore(String rootDir) throws IOException {
    rootDir_ = rootDir;
    locksMap_ = new HashMap<>();
    locksUsersCount_ = new HashMap<>();
    Files.createParentDirs(new File(getFileName("any_file")));
  }

  @Override
  public void putString(String key, String value) throws StoreException {
    putBytes(key, value.getBytes(Charsets.UTF_8));
  }

  @Override
  public String getString(String key) throws StoreException {
    return new String(getBytes(key), Charsets.UTF_8);
  }

  @Override
  public void putBytes(String key, byte[] data) throws StoreException {
    Lock lock = getLock(key);
    try {
      doWrite(key, data);
    } catch (IOException e) {
      throw new StoreException("Unable to save data", e);
    } finally {
      returnLock(lock, key);
    }
  }

  @Override
  public byte[] getBytes(String key) throws StoreException {
    Lock lock = getLock(key);
    try {
      return doRead(key);
    } catch (IOException e) {
      throw new StoreException("Unable to read data", e);
    } finally {
      returnLock(lock, key);
    }
  }

  @Override
  public void delete(String key) {
    Lock lock = getLock(key);
    try {
      doDelete(key);
    } finally {
      returnLock(lock, key);
    }
  }



  private byte[] doRead(String key) throws IOException {
    return Files.asByteSource(new File(getFileName(key))).read();
  }

  private void doWrite(String key, byte[] data) throws IOException {
    File file = new File(getFileName(key));
    file.createNewFile();
    Files.asByteSink(file).write(data);
  }

  private void doDelete(String key) {
    new File(getFileName(key)).delete();
  }

  private String getFileName(String key) {
    return Paths.get(rootDir_, key).toString();
  }

  /**
   * Store locks that are used.
   */
  private Lock getLock(String key) {
    Lock lock = null;
    synchronized (locksMap_) {
      lock = locksMap_.get(key);
      if (lock == null) {
        lock = new ReentrantLock();
        locksMap_.put(key, lock);
        locksUsersCount_.put(key, 1);
      } else {
        locksUsersCount_.put(key, locksUsersCount_.get(key) + 1);
      }
    }
    lock.lock();
    return lock;
  }

  /**
   * Remove unused locks to avoid memory leaks.
   */
  private void returnLock(Lock lock, String key) {
    lock.unlock();
    synchronized (locksMap_) {
      Integer users = locksUsersCount_.get(key);
      if (users == 1) {
        locksMap_.remove(key);
        locksUsersCount_.remove(key);
      } else {
        locksUsersCount_.put(key, users - 1);
      }
    }
  }
}
