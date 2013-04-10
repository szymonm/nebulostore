package org.nebulostore.timer;

import org.nebulostore.appcore.Message;

/**
 * Timer that is able to send timeout messages via Dispatcher queue.
 * @author Bolek Kulbabinski
 */
public interface Timer {
  /**
   * Schedules a TimeoutMessage with null content to be sent to module with {@code jobId} after
   * {@code delayMillis}.
   * @param jobId ID of JobModule that is going to receive TimeoutMessage
   * @param messageContent
   * @param delayMillis
   * @throws IllegalStateException if the timer is cancelled.
   */
  void schedule(String jobId, long delayMillis);

  /**
   * Schedules a TimeoutMessage with content {@code messageContent} to be sent to module with
   * {@code jobId} after {@code delayMillis}.
   * @param jobId ID of JobModule that is going to receive TimeoutMessage
   * @param delayMillis
   * @param messageContent
   * @throws IllegalStateException if the timer is cancelled.
   */
  void schedule(String jobId, long delayMillis, String messageContent);

  /**
   * Schedules {@code message} to be sent to dispatcher after {@code delayMillis}.
   * @param jobId ID of JobModule that is going to receive TimeoutMessage
   * @param delayMillis
   * @param message
   * @throws IllegalStateException if the timer is cancelled.
   */
  void schedule(Message message, long delayMillis);

  /**
   * Schedules {@code message} to be sent to dispatcher after {@code delayMillis} and then
   * repeatedly every {@code periodMillis}.
   * @param jobId ID of JobModule that is going to receive TimeoutMessage
   * @param delayMillis
   * @param periodMillis
   * @param message
   * @throws IllegalStateException if the timer is cancelled.
   */
  void scheduleRepeated(Message message, long delayMillis, long periodMillis);

  /**
   * Cancels all scheduled tasks on this timer.
   */
  void cancelTimer();
}
