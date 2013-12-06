package org.nebulostore.utils;

import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * {@link CompletionServiceFactory} which creates {@link ExecutorCompletionService}
 * once it is supplied executor for the first time.
 *
 * @param <V>
 *
 * @author Grzegorz Milka
 *
 */
public class SingleCompletionServiceFactory<V> implements CompletionServiceFactory<V> {
  private static final Logger LOGGER = Logger.getLogger(SingleCompletionServiceFactory.class);
  private final CompletionServiceReader<V> complServiceProxy_ = new CompletionServiceReaderProxy();
  private CompletionService<V> completionService_;

  /**
   * @return Reader for completion service once it is created.
   */
  public synchronized CompletionServiceReader<V> getCompletionServiceReader() {
    return complServiceProxy_;
  }

  @Override
  public synchronized CompletionService<V> getCompletionService(Executor executor) {
    if (completionService_ == null) {
      completionService_ = new ExecutorCompletionService<>(executor);
      notifyAll();
    }
    return completionService_;
  }

  /**
   * @author Grzegorz Milka
   */
  public class CompletionServiceReaderProxy implements CompletionServiceReader<V> {
    private CompletionService<V> cachedCompletionService_;
    @Override
    public synchronized Future<V> poll() {
      if (cachedCompletionService_ == null) {
        synchronized (SingleCompletionServiceFactory.this) {
          if (completionService_ == null) {
            return null;
          } else {
            cachedCompletionService_ = completionService_;
          }
        }
      }
      return cachedCompletionService_.poll();
    }

    @Override
    public synchronized Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException {
      if (cachedCompletionService_ == null) {
        synchronized (SingleCompletionServiceFactory.this) {
          if (completionService_ == null) {
            return null;
          } else {
            cachedCompletionService_ = completionService_;
          }
        }
      }
      return cachedCompletionService_.poll(timeout, unit);
    }

    @Override
    public synchronized Future<V> take() throws InterruptedException {
      if (cachedCompletionService_ == null) {
        LOGGER.info("cached is null");
        synchronized (SingleCompletionServiceFactory.this) {
          if (completionService_ == null) {
            SingleCompletionServiceFactory.this.wait();
          }
          LOGGER.info("compl has appeared");
          cachedCompletionService_ = completionService_;
        }
      }
      return cachedCompletionService_.take();
    }
  }

}
