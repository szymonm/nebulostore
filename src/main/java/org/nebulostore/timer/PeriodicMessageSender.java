package org.nebulostore.timer;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
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
  private static Logger logger_ = Logger.getLogger(PeriodicMessageSender.class);
  private final MessageGenerator messageGenerator_;
  /** Period in milliseconds. */
  private final Long period_;
  private final PeriodicMessageSenderVisitor visitor_ = new PeriodicMessageSenderVisitor();

  public PeriodicMessageSender(MessageGenerator messageGenerator, Long period) {
    messageGenerator_ = messageGenerator;
    period_ = period;
  }

  public PeriodicMessageSender(MessageGenerator messageGenerator, Long period,
      BlockingQueue<Message> dispatcherQueue) {
    this(messageGenerator, period);
    setOutQueue(dispatcherQueue);
    runThroughDispatcher(dispatcherQueue);
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
      logger_.debug("PMS: " + m.getClass().getSimpleName());
      outQueue_.add(m);
      endJobModule();
      TimerContext.getInstance().addDelayedMessage(System.currentTimeMillis() + period_,
          new JobInitMessage(new PeriodicMessageSender(messageGenerator_, period_)));
      return null;
    }
  }
}
