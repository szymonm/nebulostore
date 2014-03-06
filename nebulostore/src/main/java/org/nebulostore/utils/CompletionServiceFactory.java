package org.nebulostore.utils;

import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;

/**
 * Factory of CompletionService based on supplied executor.
 *
 * @param <V>
 *
 * @author Grzegorz Milka
 *
 */
public interface CompletionServiceFactory<V> {
  CompletionService<V> getCompletionService(Executor executor);
}
