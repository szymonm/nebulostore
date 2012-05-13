package org.planx.xmlstore.routing.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.kademlia.KademliaMessage;

/**
 * Listens for incoming UDP messages and provides a framework for sending
 * messages and responding to received messages. Two threads are started: One
 * that listens for incoming messages and one that handles timeout events.
 **/
public class MessageServer {

  private static Logger logger_ = Logger.getLogger(MessageServer.class);

  private static Random random = new Random();
  private final MessageFactory factory;
  private final long timeout;
  private final BlockingQueue<Message> outQueue_;
  private final BlockingQueue<Message> inQueue_;
  private final Timer timer;
  private boolean isRunning = true;
  private final Map receivers; // keeps track of registered receivers
  private final Map tasks; // keeps track of timeout events

  /**
   * Constructs a MessageServer listening on the specified UDP port using the
   * specified MessageFactory for interpreting incoming messages.
   * 
   * @param udpPort
   *          The UDP port on which to listen for incoming messages
   * @param factory
   *          Factory for creating Message and Receiver objects
   * @param timeout
   *          The timeout period in milliseconds
   * @throws SocketException
   *           if the socket could not be opened, or the socket could not bind
   *           to the specified local port
   **/
  public MessageServer(BlockingQueue<Message> outQueue,
      BlockingQueue<Message> inQueue, MessageFactory factory, long timeout)
          throws SocketException {
    this.factory = factory;
    this.timeout = timeout;

    outQueue_ = outQueue;
    inQueue_ = inQueue;

    timer = new Timer(true);
    receivers = new HashMap();
    tasks = new HashMap();

    new Thread() {
      @Override
      public void run() {
        listen();
      }
    }.start();
  }

  /**
   * Sends the specified Message and calls the specified Receiver when a reply
   * for the message is received. If <code>recv</code> is <code>null</code> any
   * reply is ignored. Returns a unique communication id which can be used to
   * identify a reply.
   **/
  public synchronized int send(KademliaInternalMessage message,
      CommAddress address, Receiver recv) throws IOException {

    logger_.debug("Message to send:  " + message.toString());
    if (!isRunning)
      throw new IllegalStateException("MessageServer not running");
    int comm = random.nextInt();
    if (recv != null) {
      Integer key = new Integer(comm);
      receivers.put(key, recv);
      TimerTask task = new TimeoutTask(comm, recv);
      timer.schedule(task, timeout);
      tasks.put(key, task);
    }
    sendMessage(comm, message, address);

    logger_.debug("Message sent:  " + message.toString());
    return comm;
  }

  /**
   * Sends a reply to the message with the specified communication id.
   **/
  public synchronized void reply(int comm, KademliaInternalMessage message,
      CommAddress address) throws IOException {
    if (!isRunning)
      throw new IllegalStateException("MessageServer not running");
    logger_.debug("Reply by message to send:  " + message.toString());
    sendMessage(comm, message, address);
    logger_.debug("Message sent:  " + message.toString());
  }

  private void sendMessage(int comm, KademliaInternalMessage message,
      CommAddress address) throws IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream dout = new DataOutputStream(bout);
    dout.writeInt(comm);
    dout.writeByte(message.code());
    message.toStream(dout);
    dout.close();
    byte[] data = bout.toByteArray();
    KademliaMessage msg = new KademliaMessage(null, address, data);
    try {
      outQueue_.put(msg);
    } catch (InterruptedException e) {
    }
  }

  private synchronized void unregister(int comm) {
    Integer key = new Integer(comm);
    receivers.remove(key);
    tasks.remove(key);
  }

  /**
   * Started in a separate thread.
   **/
  private void listen() {
    try {
      while (isRunning) {
        try {

          // Decode data into Message object

          KademliaMessage msg = ((KademliaMessage) inQueue_.take());

          ByteArrayInputStream bin = new ByteArrayInputStream(msg.getData());
          DataInputStream din = new DataInputStream(bin);
          int comm = din.readInt();
          byte messCode = din.readByte();

          KademliaInternalMessage message = factory
              .createMessage(messCode, din);

          logger_.debug("Message received: " + message.toString());

          // Create Receiver if one is supported
          Receiver recv = null;
          recv = factory.createReceiver(messCode, this);

          // If no receiver, get registered Receiver, if any
          if (recv == null) {
            synchronized (this) {
              Integer key = new Integer(comm);
              recv = (Receiver) receivers.remove(key);
              // Cancel timer if there was a registered Receiver
              if (recv != null) {
                TimerTask task = (TimerTask) tasks.remove(key);
                task.cancel();
              }
            }
          }

          // Invoke Receiver if one was found
          if (recv != null) {
            recv.receive(message, comm);
          }
        } catch (SocketException e) {
          // Socket has been closed, done by the close() method
          isRunning = false;
        } catch (IOException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
        }
      }
    } finally {
      isRunning = false;
    }
  }

  /**
   * Signals to the MessageServer thread that it should stop running.
   **/
  public synchronized void close() {
    if (!isRunning)
      throw new IllegalStateException("MessageServer not running");
    isRunning = false;
    timer.cancel();
    // socket.close(); // breaks the wait for incoming packets
    receivers.clear();
    tasks.clear();
  }

  /**
   * Task that gets called by a separate thread if a timeout for a receiver
   * occurs. When a reply arrives this task must be cancelled using the
   * <code>cancel()</code> method inherited from <code>TimerTask</code>. In this
   * case the caller is responsible for removing the task from the
   * <code>tasks</code> map.
   **/
  class TimeoutTask extends TimerTask {
    private final int comm;
    private final Receiver recv;

    public TimeoutTask(int comm, Receiver recv) {
      this.comm = comm;
      this.recv = recv;
    }

    @Override
    public void run() {
      try {
        unregister(comm);
        recv.timeout(comm);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
