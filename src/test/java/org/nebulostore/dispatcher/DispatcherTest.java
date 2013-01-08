package org.nebulostore.dispatcher;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.context.NebuloContext;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;

import static org.junit.Assert.assertTrue;

/**
 * Dispatcher test class.
 */
public class DispatcherTest {
  private BlockingQueue<Message> inQueue_;
  private BlockingQueue<Message> outQueue_;
  private Dispatcher dispatcher_;
  private Thread thread_;
  private Injector injector_ = Guice.createInjector(new NebuloContext());

  public static int staticCounter_;

  @BeforeClass
  public static void init() {
    DOMConfigurator.configure("resources/conf/log4j.xml");
  }

  @Before
  public void setUp() throws Exception {
    inQueue_ = new LinkedBlockingQueue<Message>();
    outQueue_ = new LinkedBlockingQueue<Message>();

    dispatcher_ = new Dispatcher(inQueue_, outQueue_, injector_);
    thread_ = new Thread(dispatcher_);
    thread_.start();
  }

  @After
  public void tearDown() throws Exception {
  }

  /**
   * Adds two messages with the same ID to dispatcher and checks if the same
   * thread handles both of them (via static variable). Then send a
   * JobEndMessage and verify that it was not forwarded to the worker thread.
   */
  @Test
  public void testHandlerExists() {
    /**
     * Simple module that counts messages.
     */
    class DummyModule extends JobModule {
      private int nMsgs_;

      DummyModule() {
        nMsgs_ = 0;
      }

      public void processMessage(Message message) {
        staticCounter_++;
        nMsgs_++;
        // Check if we are the only message receiver.
        assertTrue(staticCounter_ == nMsgs_);
      }

      @Override
      public void run() {
        try {
          Thread.sleep(200);
          processMessage(inQueue_.take());
          processMessage(inQueue_.take());
        } catch (InterruptedException exception) {
          return;
        }
      }
    }
    /**
     * Dummy message.
     */
    class DummyMessage extends Message {
      private static final long serialVersionUID = -8949747106617938488L;

      public DummyMessage() {
        super("1");
      }

      @Override
      public JobModule getHandler() {
        return new DummyModule();
      }

      @Override
      public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
        return visitor.visit(this);
      }
    }

    staticCounter_ = 0;
    inQueue_.add(new DummyMessage());
    inQueue_.add(new DummyMessage());
    inQueue_.add(new JobEndedMessage("1"));
    inQueue_.add(new KillDispatcherMessage());
    try {
      thread_.join();
    } catch (InterruptedException exception) {
      assertTrue(false);
    }
    // Verify that only two messages were handled by a worker thread.
    assertTrue(staticCounter_ == 2);
  }
}
