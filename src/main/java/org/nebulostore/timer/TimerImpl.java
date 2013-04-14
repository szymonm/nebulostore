package org.nebulostore.timer;

import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.dispatcher.messages.JobInitMessage;

/**
 * Wrapper for java.util.Timer.
 *
 * @author Bolek Kulbabinski
 */
public class TimerImpl implements Timer {
  private BlockingQueue<Message> dispatcherQueue_;
  private java.util.Timer javaTimer_;

  @Inject
  public TimerImpl(@Named("DispatcherQueue") BlockingQueue<Message> dispatcherQueue) {
    dispatcherQueue_ = dispatcherQueue;
    javaTimer_ = new java.util.Timer(true);
  }

  @Override
  public void schedule(String jobId, long delayMillis) {
    schedule(jobId, delayMillis, null);
  }

  @Override
  public void schedule(String jobId, long delayMillis, String messageContent) {
    javaTimer_.schedule(new DispatcherForwardingTimerTask(jobId, messageContent), delayMillis);
  }

  @Override
  public void schedule(Message message, long delayMillis) {
    javaTimer_.schedule(new DispatcherForwardingTimerTask(message), delayMillis);
  }

  @Override
  public void scheduleRepeated(Message message, long delayMillis, long periodMillis) {
    javaTimer_.scheduleAtFixedRate(new DispatcherForwardingTimerTask(message), delayMillis,
        periodMillis);
  }

  @Override
  public void scheduleRepeatedJob(Provider<? extends JobModule> provider, long delayMillis,
      long periodMillis) {
    javaTimer_.scheduleAtFixedRate(new DispatcherGeneratingTimerTask(provider), delayMillis,
        periodMillis);
  }

  @Override
  public void cancelTimer() {
    javaTimer_.cancel();
  }

  /**
   * @author Bolek Kulbabinski
   */
  private class DispatcherForwardingTimerTask extends TimerTask {
    private final Message message_;

    public DispatcherForwardingTimerTask(String jobId, String messageContent) {
      message_ = new TimeoutMessage(jobId, messageContent);
    }

    public DispatcherForwardingTimerTask(Message message) {
      message_ = message;
    }

    @Override
    public void run() {
      dispatcherQueue_.add(message_);
    }
  }

  /**
   * @author Bolek Kulbabinski
   */
  private class DispatcherGeneratingTimerTask extends TimerTask {
    private final Provider<? extends JobModule> provider_;

    public DispatcherGeneratingTimerTask(Provider<? extends JobModule> provider) {
      provider_ = provider;
    }

    @Override
    public void run() {
      dispatcherQueue_.add(new JobInitMessage(provider_.get()));
    }
  }
}
