package org.nebulostore.broker;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.nebulostore.appcore.InstanceID;
import org.nebulostore.appcore.Message;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.broker.AsynchronousMessagesMessage;
import org.nebulostore.communication.messages.broker.GetAsynchronousMessagesMessage;
import org.nebulostore.communication.messages.broker.GotAsynchronousMessagesMessage;
import org.nebulostore.communication.messages.broker.asynchronous.AsynchronousMessage;
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Simple unit test for GetAsynchronousMessagesModule.
 * @author szymonmatejczyk
 */
public final class GetAsynchronousMessagesModuleTest {

  @Before
  public void setUp() {
    DOMConfigurator.configure("resources/conf/log4j.xml");
  }

  @Test
  public void testSimple() {
    BlockingQueue<Message> networkQueue = new LinkedBlockingQueue<Message>();
    BlockingQueue<Message> inQueue = new LinkedBlockingQueue<Message>();
    BlockingQueue<Message> outQueue = new LinkedBlockingQueue<Message>();

    BrokerContext.getInstance().setInstanceID(new InstanceID(new CommAddress(null)));
    InstanceID synchroPeerId = new InstanceID(new CommAddress(null));
    GetAsynchronousMessagesModule module = new GetAsynchronousMessagesModule(outQueue, networkQueue,
        outQueue, BrokerContext.getInstance(), synchroPeerId);
    module.setInQueue(inQueue);

    new Thread(module).start();

    Message msg;

    try {
      msg = outQueue.take();
    } catch (InterruptedException exception) {
      assert false;
      return;
    }
    assertTrue(msg instanceof JobInitMessage);

    String jobId = ((JobInitMessage) msg).getId();

    inQueue.add(msg);

    try {
      msg = networkQueue.take();
    } catch (InterruptedException exception) {
      assert false;
      return;
    }

    assertTrue(msg instanceof GetAsynchronousMessagesMessage);
    GetAsynchronousMessagesMessage gam = (GetAsynchronousMessagesMessage) msg;
    assertEquals(gam.getId(), jobId);
    assertTrue(gam.getDestinationAddress() == synchroPeerId.getAddress());
    assertTrue(gam.getRecipient() == BrokerContext.getInstance().instanceID_);

    LinkedList<AsynchronousMessage> list = new LinkedList<AsynchronousMessage>();
    AsynchronousMessagesMessage messages = new AsynchronousMessagesMessage(jobId, null, null,
        list);
    inQueue.add(messages);

    try {
      msg = networkQueue.take();
    } catch (InterruptedException exception) {
      assert false;
      return;
    }

    assertTrue(msg instanceof GotAsynchronousMessagesMessage);
    assertEquals(((GotAsynchronousMessagesMessage) msg).getId(), jobId);

    try {
      msg = outQueue.take();
    } catch (InterruptedException exception) {
      assert false;
      return;
    }

    assertTrue(msg instanceof AsynchronousMessagesMessage);
    AsynchronousMessagesMessage m = (AsynchronousMessagesMessage) msg;
    assert m.getId().equals("parentId");
    assert m.getMessages() == list;

    try {
      msg = outQueue.take();
    } catch (InterruptedException exception) {
      assert false;
      return;
    }

    assertTrue(msg instanceof JobEndedMessage);
    assertEquals(((JobEndedMessage) msg).getId(), jobId);
  }

}
