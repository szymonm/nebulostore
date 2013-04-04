package org.nebulostore.timer;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.GlobalContext;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.TimeoutMessage;

/**
 * Context for delayed messages(timer module).
 * @author szymonmatejczyk
 */
public final class TimerContext {
  private static Logger logger_ = Logger.getLogger(TimerContext.class);

  private final PriorityQueue<TimedMessage> delayedMessagesQueue_;
  private static TimerContext instance_;

  TimerModule timerModule_;


  final Lock lock_ = new ReentrantLock();
  final Condition waitingOnNext_ = lock_.newCondition();

  private TimerContext() {
    delayedMessagesQueue_ = new PriorityQueue<TimedMessage>();
  }

  public synchronized void addDelayedMessage(Long time, Message message) {
    lock_.lock();
    Long curNextTime = nextMessageTime();
    delayedMessagesQueue_.add(new TimedMessage(time, message));
    // Runs timer automatically.
    if (timerModule_ == null) {
      timerModule_ = new TimerModule(getDispatcherQueue());
    } else {
      if ((curNextTime != null) && (time < curNextTime)) {
        waitingOnNext_.signal();
      }
    }
    lock_.unlock();
  }

  public void notifyWithTimeoutMessageAfter(String jobId, long delayMs) {
    addDelayedMessage(System.currentTimeMillis() + delayMs, new TimeoutMessage(jobId));
  }

  public void cancelNotifications(String jobId) {
    cancelDelayedMessages(jobId);
  }

  public synchronized void cancelDelayedMessages(String jobId) {
    lock_.lock();
    Iterator<TimedMessage> it = delayedMessagesQueue_.iterator();
    while (it.hasNext()) {
      TimedMessage message = it.next();
      if (message.message_.getId().equals(jobId)) {
        it.remove();
      }
    }
    lock_.unlock();
  }

  /**
   * Returns time of next message in the queue or null if queue is empty.
   */
  synchronized Long nextMessageTime() {
    if (delayedMessagesQueue_.peek() != null) {
      return delayedMessagesQueue_.peek().time_;
    } else {
      return null;
    }
  }

  /**
   * Removes next TimedMessage from the queue and returns it's message or null if queue is empty.
   */
  synchronized Message pollNextMessage() {
    TimedMessage tm = delayedMessagesQueue_.poll();
    if (tm == null) {
      return null;
    } else {
      return tm.message_;
    }
  }

  public static TimerContext getInstance() {
    if (instance_ == null) {
      instance_ = new TimerContext();
    }

    return instance_;
  }

  /**
   * Delayed message class.
   */
  private class TimedMessage implements Comparable<TimedMessage> {
    private final Long time_;
    private final Message message_;

    public TimedMessage(Long time, Message message) {
      time_ = time;
      message_ = message;
    }

    @Override
    public int compareTo(TimedMessage o) {
      return this.time_.compareTo(o.time_);
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + getOuterType().hashCode();
      result = (prime * result) + ((message_ == null) ? 0 : message_.hashCode());
      result = (prime * result) + ((time_ == null) ? 0 : time_.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      TimedMessage other = (TimedMessage) obj;
      if (!getOuterType().equals(other.getOuterType())) {
        return false;
      }
      if (message_ == null) {
        if (other.message_ != null) {
          return false;
        }
      } else if (!message_.equals(other.message_)) {
        return false;
      }
      if (time_ == null) {
        if (other.time_ != null) {
          return false;
        }
      } else if (!time_.equals(other.time_)) {
        return false;
      }
      return true;
    }

    private TimerContext getOuterType() {
      return TimerContext.this;
    }
  }

  private BlockingQueue<Message> getDispatcherQueue() {
    if (GlobalContext.getInstance().getDispatcherQueue() == null) {
      logger_.error("Dispatcher queue not set up.");
    }
    return GlobalContext.getInstance().getDispatcherQueue();
  }
}

