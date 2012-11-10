package org.nebulostore.timer;

import java.util.concurrent.BlockingQueue;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.dispatcher.messages.JobInitMessage;

/**
 * Module to send periodically a message.
 * @author szymonmatejczyk
 */
public class PeriodicMessageSender extends JobModule {
  private final IMessageGenerator messageGenerator_;
  private final Long period_;

  private final PeriodicMessageSenderVisitor visitor_ = new PeriodicMessageSenderVisitor();

  public PeriodicMessageSender(IMessageGenerator messageGenerator, Long period,
      BlockingQueue<Message> dispatcherQueue) {
    messageGenerator_ = messageGenerator;
    period_ = period;
    setOutQueue(dispatcherQueue);
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  /**
   * Visitor.
   */
  private class PeriodicMessageSenderVisitor extends MessageVisitor<Void> {
    @Override
    public Void visit(JobInitMessage message) {
      jobId_ = message.getId();
      Message m = messageGenerator_.generate();
      outQueue_.add(m);
      TimerContext.getInstance().addDelayedMessage(period_, message);
      endJobModule();
      return null;
    }
  }
}
