package org.nebulostore.appcore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * ReturningJobModule - base class for JobModules that return a result (e.g. all API methods).
 * @param <R>
 *    return type.
 */
public abstract class ReturningJobModule<R> extends JobModule {

  protected R result_;
  protected NebuloException error_;
  protected Semaphore mutex_;

  private static Logger logger_ = Logger.getLogger(ReturningJobModule.class);

  protected ReturningJobModule() {
    mutex_ = new Semaphore(1);
    mutex_.tryAcquire();
    if (mutex_.availablePermits() != 0) {
      // This should not happen.
      logger_.fatal("Could not initialize semaphore");
    }
  }

  /*
   * This method is BLOCKING!
   * It blocks for at most timeoutSec seconds and throws exception when the result is not ready
   * within this time limit.
   */
  public R getResult(int timeoutSec) throws NebuloException {
    try {
      if (!mutex_.tryAcquire(timeoutSec, TimeUnit.SECONDS)) {
        throw new NebuloException("Timeout");
      }
    } catch (InterruptedException exception) {
      throw new NebuloException("Interrupted while waiting for result", exception);
    }
    if (error_ != null) {
      throw error_;
    } else {
      return result_;
    }
  }

  protected void returnSuccess(R result) {
    result_ = result;
    // Make result available.
    mutex_.release();
  }

  protected void endWithSuccess(R result) {
    returnSuccess(result);
    // End thread and remove from Dispatcher's queue.
    endJobModule();
  }

  protected void endWithError(NebuloException error) {
    error_ = error;
    // Make result available.
    mutex_.release();
    // End thread and remove from Dispatcher's queue.
    endJobModule();
  }

}
