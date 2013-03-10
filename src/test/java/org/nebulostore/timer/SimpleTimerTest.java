package org.nebulostore.timer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nebulostore.appcore.GlobalContext;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.context.DefaultTestContext;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.dispatcher.Dispatcher;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;

import static org.junit.Assert.assertTrue;

/**
 * Simple timer tests.
 * @author szymonmatejczyk
 */
public class SimpleTimerTest {

  @Before
  public void setUp() {
    DOMConfigurator.configure("resources/conf/log4j.xml");
  }

  @Ignore
  @Test
  public void testWithDispatcher() {
    BlockingQueue<Message> dispatcherQueue = new LinkedBlockingQueue<Message>();
    Injector injector = Guice.createInjector(new DefaultTestContext());
    Dispatcher dispatcher = new Dispatcher(dispatcherQueue, null, injector);
    GlobalContext.getInstance().setDispatcherQueue(dispatcherQueue);

    InitSimpleTimerTestMessage message = new InitSimpleTimerTestMessage("jobId");
    SimpleTimerTestModule module = message.handler_;
    dispatcherQueue.add(message);

    Thread dispatcherThread = new Thread(dispatcher);
    dispatcherThread.start();

    try {
      module.getResult(5);
    } catch (NebuloException e) {
      e.printStackTrace();
      assertTrue(false);
    } finally {
      dispatcherQueue.add(new KillDispatcherMessage());
      try {
        dispatcherThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
