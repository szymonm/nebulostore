package pl.edu.uw.mimuw.nebulostore.appcore;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Marcin Walas
 */
public class QueueMerger implements Runnable {

  private static long waitConst_ = 10;

  private final List<BlockingQueue<Message>> inQueues_;
  private final BlockingQueue<Message> outQueue_;

  public QueueMerger(List<BlockingQueue<Message>> inQueues,
      BlockingQueue<Message> outQueue) {
    inQueues_ = new LinkedList<BlockingQueue<Message>>(inQueues);
    outQueue_ = outQueue;
  }

  @Override
  public void run() {
    // TODO MBW: This is active waiting implementation - very bad...
    // Bolek: rewrite it.
    while (true) {
      Message msg = null;
      for (BlockingQueue<Message> queue : inQueues_) {

        try {
          msg = queue.poll(waitConst_, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          // TODO MBW: I don't know what to do with this exception.
          // To be investigated.

          e.printStackTrace();
        }
        if (msg != null) {
          break;
        }

      }
      if (msg == null) {
        continue;
      }
      // TODO MBW: Marking source queue in msg
      outQueue_.add(msg);
    }

  }

}
