package org.nebulostore.timer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.dispatcher.messages.JobInitMessage;

/**
 * Module handling delayed messages in TimerContext. Is run when TimerContext contains any messages.
 * @author szymonmatejczyk
 */
public class TimerModule extends JobModule {
  private static Logger logger_ = Logger.getLogger(TimerModule.class);

  private final MessageVisitor<Void> visitor_ = new TimerModuleVisitor();


  TimerModule(BlockingQueue<Message> dispatcherQueue) {
    setOutQueue(dispatcherQueue);
    runThroughDispatcher(outQueue_);
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  @Override
  protected void endJobModule() {
    TimerContext context = TimerContext.getInstance();
    context.timerModule_ = null;
    super.endJobModule();
  }

  /**
   * Visitor.
   */
  private class TimerModuleVisitor extends MessageVisitor<Void> {
    TimerContext context_ = TimerContext.getInstance();
    @Override
    public Void visit(JobInitMessage message) {
      jobId_ = message.getId();
      logger_.debug("timer module started");
      context_.lock_.lock();
      try {
        while (context_.nextMessageTime() != null) {
          if (context_.nextMessageTime() <= System.currentTimeMillis()) {
            logger_.debug("sending delayed message");
            outQueue_.add(context_.pollNextMessage());
          } else {
            context_.waitingOnNext_.await(
                context_.nextMessageTime() - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS);
          }
        }
      } catch (InterruptedException e) {
        error("Interrupted");
      } finally {
        context_.lock_.unlock();
      }
      endJobModule();
      return null;
    }

    private void error(String message) {
      logger_.warn(message);
    }
  }
}
