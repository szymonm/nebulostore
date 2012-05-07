package org.nebulostore.timer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.nebulostore.appcore.GlobalContext;
import org.nebulostore.appcore.Message;
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

  @Test
  public void testWithDispatcher() {
    BlockingQueue<Message> dispatcherQueue = new LinkedBlockingQueue<Message>();
    Dispatcher dispatcher = new Dispatcher(dispatcherQueue, null);
    GlobalContext.getInstance().setDispatcherQueue(dispatcherQueue);

    InitSimpleTimerTestMessage message = new InitSimpleTimerTestMessage();
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
