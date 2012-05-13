package org.nebulostore.appcore;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.dht.BdbDHTTestServer;
import org.nebulostore.communication.dht.KademliaDHTTestServer;
import org.nebulostore.communication.messages.MessagesTestServer;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;
import org.nebulostore.testing.ServerTestingModule;
import org.nebulostore.testing.pingpong.PingPongServer;

/**
 * Class to run test server.
 * 
 * @author szymonmatejczyk
 */
public final class TestingPeer extends Peer {
  private static Logger logger_ = Logger.getLogger(TestingPeer.class);

  private TestingPeer() {
  }

  public static void main(String[] args) {
    DOMConfigurator.configure("resources/conf/log4j.xml");
    BigInteger appKey = BigInteger.ZERO;
    if (args.length < 1) {
      // Random AppKey if not provided.
      appKey = CryptoUtils.getRandomId();
    } else {
      appKey = new BigInteger(args[0]);
    }
    startPeer(new AppKey(appKey));
    try {
      // Waiting for peer initialization
      Thread.sleep(7000);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
      System.exit(-1);
    }

    // Insert test modules you want to be ran below.
    runTest(new PingPongServer(), "PingPong");
    logger_.info("Finished PingPong test, performing DHT tests...");

    dhtTests();

    logger_.info("Finished DHT tests, performing messages tests...");

    //    messagesTests();

    logger_.info("All tests finished");

    dispatcherInQueue_.add(new KillDispatcherMessage());
    finishPeer();
  }

  private static void messagesTests() {
    int count = 15;
    int maxPeers = 15;
    int minPeers = 5;
    int stepPeers = 3;

    int minEpoches = 10;
    int maxEpoches = 30;
    int stepEpoches = 2;

    int minMessagesInPhase = 10;
    int maxMessagesInPhase = 100;
    int stepMessagesInPhase = 25;

    for (int epoches = minEpoches; epoches < maxEpoches; epoches += stepEpoches) {
      for (int peers = minPeers; peers < maxPeers; peers += stepPeers) {
        for (int messagesInPhase = minMessagesInPhase; messagesInPhase < maxMessagesInPhase; messagesInPhase += stepMessagesInPhase) {
          for (int i = 0; i < count; i++) {
            String messagesDesc = "Messages test [" + epoches + "\t" + peers + "\t" +
                messagesInPhase + "] - " + "count: " + i;
            runTest(new MessagesTestServer(epoches, 17, peers, 100,
                messagesDesc, messagesInPhase), "MessagesTestServer " + messagesDesc + "\t" +
                    i);
            logger_
            .info("Finished Messages test. Moving to the next test.");
          }
        }
      }
    }

  }

  private static void dhtTests() {
    int count = 15;

    int peersFound = 17;

    int maxPeers = 15;
    int minPeers = 5;
    int stepPeers = 3;

    int minEpoches = 5;
    int maxEpoches = 30;
    int stepEpoches = 2;

    int minKeysMultiplier = 2;
    int maxKeysMultiplier = 16;
    int stepKeysMultiplier = 2;

    for (int epoches = minEpoches; epoches < maxEpoches; epoches += stepEpoches) {
      for (int peers = minPeers; peers < maxPeers; peers += stepPeers) {
        /*
        if (peers == 11) {
          minKeysMultiplier = 8;
        } else {
          minKeysMultiplier = 2;
        }
         */
        for (int keysMultiplier = minKeysMultiplier; keysMultiplier < maxKeysMultiplier; keysMultiplier += stepKeysMultiplier) {
          for (int i = 0; i < count; i++) {
            String bdbDesc = "Bdb test [" + epoches + "\t" + peers + "\t" +
                keysMultiplier + "] - count: " + i;
            runTest(new BdbDHTTestServer(epoches, peers + 5, peers,
                keysMultiplier, bdbDesc), "BdbDHTTestServer " + bdbDesc + "\t" +
                    i);
            logger_
            .info("Finished Bdb DHT Test, performing Kademlia DHT Test...");
          }
          for (int i = 0; i < count; i++) {
            String kadDesc = "Kademlia test [" + epoches + "\t" + peers + "\t" +
                keysMultiplier + "] - count: " + i;
            runTest(new KademliaDHTTestServer(epoches, peers + 5, peers,
                keysMultiplier, kadDesc), "KademliaDHTTestServer " + kadDesc +
                "\t" + i);
            logger_.info("DHT Test finished.");

          }
        }
      }
    }

  }

  private static void runTest(ServerTestingModule testModule, String testName) {
    try {
      testModule.runThroughDispatcher(dispatcherInQueue_, testName + " server");
      testModule.getResult();
    } catch (NebuloException exception) {
      testModule.endJobModule();
      logger_.error("NebuloException at test " + testName + " : " +
          exception.getMessage());
    }
  }

}
