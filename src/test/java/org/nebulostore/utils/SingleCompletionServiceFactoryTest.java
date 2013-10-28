package org.nebulostore.utils;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SingleCompletionServiceFactoryTest {
  private SingleCompletionServiceFactory<Boolean> completionServiceFactory_;

  private Executor executor_;


  @Before
  public void setUp() throws Exception {
    completionServiceFactory_ = new SingleCompletionServiceFactory<>();
    executor_ = Executors.newFixedThreadPool(1);
  }

  @Test(timeout = 100)
  public void shouldReturnResult() throws InterruptedException, ExecutionException {
    completionServiceFactory_.getCompletionService(executor_).submit(new SimpleTask());
    Future<Boolean> future = completionServiceFactory_.getCompletionServiceReader().take();
    assertEquals(true, future.get());
  }

  @Test(timeout = 100)
  public void shouldReturnException() throws InterruptedException, ExecutionException {
    completionServiceFactory_.getCompletionService(executor_).submit(new FaultyTask());
    Future<Boolean> future = completionServiceFactory_.getCompletionServiceReader().take();
    try {
      future.get();
    } catch (ExecutionException e) {
      assertTrue(e.getCause() instanceof IOException);
    }
  }

  private class SimpleTask implements Callable<Boolean> {
    @Override
    public Boolean call() {
      return true;
    }
  }

  private class FaultyTask implements Callable<Boolean> {
    @Override
    public Boolean call() throws Exception {
      throw new IOException();
    }
  }
}
