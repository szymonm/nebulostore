package org.nebulostore.query.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DQLExecutor {

  ExecutorService threadPool;

  public DQLExecutor(int threadPoolSize) {
    threadPool = Executors.newFixedThreadPool(threadPoolSize);
  }
}
