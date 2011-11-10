package pl.edu.uw.mimuw.nebulostore.dispatcher;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pl.edu.uw.mimuw.nebulostore.appcore.Message;
import pl.edu.uw.mimuw.nebulostore.appcore.MessageVisitor;
import pl.edu.uw.mimuw.nebulostore.appcore.Module;
import pl.edu.uw.mimuw.nebulostore.appcore.messages.JobEndedMessage;

/**
 * Dispatcher.
 *
 */
public class Dispatcher extends Module {

  /**
   * Visitor class. Contains logic for handling messages depending
   * on their types.
   */
  public class MessageDispatchVisitor extends MessageVisitor {
    /*
     * Special handling for JobEndedMessage.
     * Remove MSG_ID from Dispatcher's map.
     */
    @Override
    public void visit(JobEndedMessage message) {
      String jobId = message.getId();
      if (workersQueues_.containsKey(jobId)) {
        workersQueues_.remove(jobId);
      }
      if (workersThreads_.containsKey(jobId)) {
        workersThreads_.remove(jobId);
      }
    }

    /*
     * General behavior - forwarding messages.
     */
    @Override
    public void visit(Message message) {
      String jobId = message.getId();
      if (!workersQueues_.containsKey(jobId)) {
        // Spawn a new thread to handle the message.
        try {
          Module handler = message.getHandler();
          BlockingQueue<Message> newInQueue = new LinkedBlockingQueue<Message>();
          handler.setInQueue(newInQueue);
          // TODO(bolek): Is outQueue needed?
          handler.setOutQueue(inQueue_);
          workersQueues_.put(jobId, newInQueue);
          Thread newThread = new Thread(handler);
          workersThreads_.put(jobId, newThread);
          newThread.start();
        } catch (Exception e) {
          // TODO(bolek): Log it or better throw again.
        }
      }
      // Delegate message to a waiting worker thread.
      workersQueues_.get(jobId).add(message);
    }
  }

  /**
   *
   * @param inQueue Input message queue.
   * @param outQueue Output message queue (usually other module's input queue).
   */
  public Dispatcher(BlockingQueue<Message> inQueue,
                    BlockingQueue<Message> outQueue) {
    super(inQueue, outQueue);
    visitor_ = new MessageDispatchVisitor();
    workersQueues_ = new TreeMap<String, BlockingQueue<Message>>();
    workersThreads_ = new TreeMap<String, Thread>();
  }

  @Override
  public void processMessage(Message message) {
    // Handling logic lies inside our visitor class.
    message.accept(visitor_);
  }

  // TODO(bolek): This will later be invoked by an API call.
  // (Now useful for testing purposes.)
  // Wait for all sub-threads to end.
  public void die() {
    Thread[] threads = workersThreads_.values().toArray(new Thread[0]);
    for (int i = 0; i < threads.length; ++i) {
      try {
        threads[i].join();
      } catch (Exception e) {
      }
    }
  }

  private Map<String, BlockingQueue<Message>> workersQueues_;
  private Map<String, Thread> workersThreads_;
  private MessageVisitor visitor_;
}
