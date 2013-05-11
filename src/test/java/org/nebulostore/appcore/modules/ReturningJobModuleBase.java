package org.nebulostore.appcore.modules;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.messaging.MessageVisitor;

import static org.junit.Assert.fail;

/**
 * Base class for ReturningJobModuleTest and TwoStepReturningJobModuleTest.
 * @author Bolek Kulbabinski
 */
public abstract class ReturningJobModuleBase {
  /**
   * Helper message class.
   */
  protected class TestMessage extends Message {
    private static final long serialVersionUID = 6355287939938856475L;
    @Override
    public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
      return visitor.visit(this);
    }
  }

  protected static final Integer SUCCESS_VALUE = 123;
  protected static final String ERROR_VALUE = "my_error";
  protected static final int TIMEOUT_SEC = 10;

  protected BlockingQueue<Message> inQueue_;
  protected BlockingQueue<Message> outQueue_;

  @Before
  public void initQueues() {
    inQueue_ = new LinkedBlockingQueue<Message>();
    outQueue_ = new LinkedBlockingQueue<Message>();
  }

  protected Thread setupModule(Module module) {
    module.setInQueue(inQueue_);
    module.setOutQueue(outQueue_);
    Thread thread = new Thread(module);
    thread.start();
    inQueue_.add(new TestMessage());
    return thread;
  }

  protected void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      fail();
    }
  }
}
