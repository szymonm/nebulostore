package org.nebulostore.utils;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Reduction of CompletionService interface to reading functions.
 *
 * @param <V>
 *
 * @author Grzegorz Milka
 *
 */
public interface CompletionServiceReader<V> {
  Future<V> poll();

  Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException;

  Future<V> take() throws InterruptedException;
}
