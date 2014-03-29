package org.nebulostore.communication.netutils.remotemap;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import org.nebulostore.utils.Pair;

/**
 * {@link RemoteMap} represented by in-memory map structure.
 *
 * @author Grzegorz Milka
 *
 */
public class InMemoryMap implements RemoteMap {
  private static final Logger LOGGER = Logger.getLogger(InMemoryMap.class);
  private final Map<Pair<Integer, Serializable>, Serializable> map_ = new ConcurrentHashMap<>();
  private final ReadWriteLock readWriteLock_ = new ReentrantReadWriteLock(true);
  private final Lock readLock_;
  private final Lock writeLock_;

  public InMemoryMap() {
    readLock_ = readWriteLock_.readLock();
    writeLock_ = readWriteLock_.writeLock();
  }

  @Override
  public Serializable get(int type, Serializable key) {
    LOGGER.trace(String.format("get(%d, %s)", type, key));
    readLock_.lock();
    try {
      return map_.get(new Pair<Integer, Serializable>(type, key));
    } finally {
      readLock_.unlock();
    }

  }

  @Override
  public void put(int type, Serializable key, Serializable value) {
    LOGGER.trace(String.format("put(%d, %s, %s)", type, key, value));
    readLock_.lock();
    try {
      map_.put(new Pair<Integer, Serializable>(type, key), value);
    } finally {
      readLock_.unlock();
    }
  }

  @Override
  public void performTransaction(int type, Serializable key, Transaction transaction)
      throws IOException {
    LOGGER.trace(String.format("performTransaction(%d, %s, %s)", type, key, transaction));
    writeLock_.lock();
    try {
      map_.put(new Pair<Integer, Serializable>(type, key), transaction.performTransaction(
          type, key, map_.get(new Pair<Integer, Serializable>(type, key))));
    } finally {
      writeLock_.unlock();
    }
  }
}
