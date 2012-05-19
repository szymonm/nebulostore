package org.nebulostore.dispatcher;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;

/**
 * Dispatcher.
 *
 */
public class Dispatcher extends Module {

  private final Map<String, BlockingQueue<Message>> workersQueues_;
  private final Map<String, Thread> workersThreads_;
  private final MessageVisitor<?> visitor_;

  private static Logger logger_ = Logger.getLogger(Dispatcher.class);

  /**
   * Visitor class. Contains logic for handling messages depending
   * on their types.
   */
  public class MessageDispatchVisitor extends MessageVisitor<Void> {
    /*
     * Special handling for JobEndedMessage.
     * Remove MSG_ID from Dispatcher's map.
     */
    @Override
    public Void visit(JobEndedMessage message) {
      logger_.debug("Got job ended message " + message.getId());
      String jobId = message.getId();
      if (workersQueues_.containsKey(jobId)) {
        workersQueues_.remove(jobId);
      }
      if (workersThreads_.containsKey(jobId)) {
        // workersThreads_.get(jobId).interrupt();
        workersThreads_.remove(jobId);
      }
      return null;
    }

    /*
     * End dispatcher.
     */
    @Override
    public Void visit(KillDispatcherMessage message) throws NebuloException {
      Thread[] threads = workersThreads_.values().toArray(new Thread[0]);
      logger_.debug("Quitting dispatcher, waiting for " + String.valueOf(threads.length) +
          " job threads.");
      for (int i = 0; i < threads.length; ++i) {
        try {
          threads[i].join();
        } catch (InterruptedException exception) {
          continue;
        }
      }
      endModule();
      return null;
    }

    /*
     * General behavior - forwarding messages.
     */
    @Override
    public Void visitDefault(Message message) throws NebuloException {
      String jobId = message.getId();
      logger_.debug("Received message with jobID: " + jobId + " and class name: " +
          message.getClass().getName());

      if (!workersQueues_.containsKey(jobId)) {
        // Spawn a new thread to handle the message.
        try {
          JobModule handler = message.getHandler();
          BlockingQueue<Message> newInQueue = new LinkedBlockingQueue<Message>();
          handler.setInQueue(newInQueue);
          handler.setOutQueue(inQueue_);
          // Network queue is dispatcher's out queue.
          handler.setNetworkQueue(outQueue_);
          workersQueues_.put(jobId, newInQueue);
          Thread newThread = new Thread(handler, "Nebulostore.Dispatcher.Job." + handler.getClass().getSimpleName() + "." + jobId);
          workersThreads_.put(jobId, newThread);
          newThread.start();
          logger_.debug("Starting new thread.");
        } catch (NebuloException exception) {
          // TODO(bolek): Better log it here than deeper in run().
          throw exception;
        }
      }
      // Delegate message to a waiting worker thread.
      workersQueues_.get(jobId).add(message);
      return null;
    }
  }

  /**
   *
   * @param inQueue Input message queue.
   * @param outQueue Output message queue, which is also later passed to newly
   *                 created tasks (usually network's inQueue).
   */
  public Dispatcher(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue) {
    super(inQueue, outQueue);
    visitor_ = new MessageDispatchVisitor();
    workersQueues_ = new TreeMap<String, BlockingQueue<Message>>();
    workersThreads_ = new TreeMap<String, Thread>();
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    // Handling logic lies inside our visitor class.
    message.accept(visitor_);
  }
}
