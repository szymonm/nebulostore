package org.nebulostore.communication.kademlia;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.dht.ValueDHT;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.OutDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;

public class KademliaPerfTest {

  static Logger logger_ = Logger.getLogger(KademliaPerfTest.class);
  CommunicationPeer communicationPeer_;

  public static void main(String[] args) {
    new KademliaPerfTest().run();
  }

  private void run() {
    DOMConfigurator.configure("resources/conf/log4j.xml");

    logger_.info("Starting up application...");

    BlockingQueue<Message> inQueue = new LinkedBlockingQueue<Message>();
    BlockingQueue<Message> outQueue = new LinkedBlockingQueue<Message>();

    try {
      communicationPeer_ = new CommunicationPeer(inQueue, outQueue);
    } catch (NebuloException e) {
      e.printStackTrace();
      System.exit(-1);
    }

    new Thread(communicationPeer_).start();

    List<Integer> foundPeers = new LinkedList<Integer>();

    // FOR boostrap process to take place
    try {
      Thread.sleep(7000);
    } catch (InterruptedException e1) {
    }

    logger_.info("Starting performance tests...");
    putTest(20, inQueue, outQueue);
    putTest(200, inQueue, outQueue);
    putTest(1000, inQueue, outQueue);
    putTest(1000, inQueue, outQueue);
    putTest(1000, inQueue, outQueue);
    putTest(1000, inQueue, outQueue);

    while (true) {
      try {
        Thread.sleep(2500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      logger_.debug(communicationPeer_.getDHTPeer().toString());

    }

  }

  private void putTest(int testSize, BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue) {
    // Putting
    long startTime = System.currentTimeMillis();
    boolean allFinished = false;

    int issued = 0;
    Set<String> finished = new HashSet<String>();
    Set<String> testSet = new HashSet<String>();

    Random random = new Random(startTime);

    while ((issued < testSize) || (finished.size() < testSet.size())) {

      if (issued < testSize) {
        issued++;
        byte[] rbytes = new byte[5];
        random.nextBytes(rbytes);
        testSet.add(new String(rbytes));
        inQueue.add(new PutDHTMessage(new String(rbytes), new KeyDHT(
            new String(rbytes)), new ValueDHT(random.nextLong())));
      }

      Message msg = null;
      try {
        msg = outQueue.poll(5, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
      }
      if (msg != null) {
        logger_.debug("Got message of class " + msg.getClass());

        if (msg instanceof OutDHTMessage && msg instanceof OkDHTMessage) {
          finished.add(((OkDHTMessage) msg).getId());
//          logger.debug(communicationPeer.getDHTPeer().toString());
        }
        if (msg instanceof OutDHTMessage && msg instanceof ErrorDHTMessage) {
          logger_.error("Got ErrorDHTMessage with ",
              ((ErrorDHTMessage) msg).getException());
        }
      }

      if (msg != null || issued < testSize) {
        logger_.debug("Finished: " + finished.size() + " / " + testSet.size() +
            " Issued: " + issued + " / " + testSize);
      }

    }
    long endTime = System.currentTimeMillis();
    logger_.info("test results: put of " + testSize + " messages finished in " +
        (endTime - startTime) + " per sec: " + ((double)testSize) * 1000.0 / (double)(endTime - startTime) );

  }
}
