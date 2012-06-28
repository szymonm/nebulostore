package org.nebulostore.appcore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Returning job module with communication in the middle. Returns semi result and waits for
 * answer from parent module. When obtains answer, runs performSecondPhase(answer) implemented
 * in subclass which should finish with either endWithSuccess or endWithError.
 *
 * @author szymonmatejczyk
 *
 * @param <R> Final result type.
 * @param <SR> Semi result type.
 * @param <A> Anwer result type.
 */
public abstract class TwoStepReturningJobModule<R, SR, A> extends ReturningJobModule<R> {

  private static Logger logger_ = Logger.getLogger(TwoStepReturningJobModule.class);

  protected SR semiResult_;
  protected Semaphore answerMutex_;
  protected A answer_;
  protected boolean afterFirstResult_;

  public TwoStepReturningJobModule() {
    super();
    answerMutex_ = new Semaphore(0);
    if (answerMutex_.availablePermits() != 0) {
      logger_.error("Failed to initialize mutex.");
    }
  }

  /**
   * Sets semiResult_. After that, module is waiting(on answerMutex_) for an answer
   * from parent module(see answer(A) method).
   *
   * @param semiResult
   */
  protected void returnSemiResult(SR semiResult) {
    semiResult_ = semiResult;
    mutex_.release();
    try {
      answerMutex_.acquire();
      mutex_.acquire();
      performSecondPhase(answer_);
    } catch (InterruptedException exception) {
      endWithError(new NebuloException("Interrupted", exception));
    }
  }

  protected abstract void performSecondPhase(A answer);

  /**
   * Blocking - waits for SemiResult returned by module at most timeout seconds.
   *
   * @param timeout
   * @return
   * @throws NebuloException
   */
  public SR getSemiResult(int timeout) throws NebuloException {
    try {
      if (!mutex_.tryAcquire(timeout, TimeUnit.SECONDS))
          throw new NebuloException("Timeout");
    } catch (InterruptedException exception) {
      throw new NebuloException("Interrupted", exception);
    }
    if (error_ != null) {
      throw error_;
    } else {
      SR result = semiResult_;
      mutex_.release();
      afterFirstResult_ = true;
      return result;
    }
  }

  public void answer(A answer) {
    answer_ = answer;
    answerMutex_.release();
  }

  @Override
  public R getResult(int timeoutSec) throws NebuloException {
    if (!afterFirstResult_) {
      throw new UnsupportedOperationException("Waiting for second result before first.");
    }
    return super.getResult(timeoutSec);
  }


}
