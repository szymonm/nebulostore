package org.nebulostore.appcore;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.dht.KademliaDHTTestServer;
import org.nebulostore.communication.messages.MessagesTestServer;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;
import org.nebulostore.query.trivial.TrivialQueryTestServer;
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
      ApiFacade.putKey(new AppKey(appKey));
    } catch (NebuloException e) {
      logger_.error(e);
    }

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

    //dhtTests();

    logger_.info("Finished DHT tests, performing messages tests...");

    //messagesTests();

    runTest(new TrivialQueryTestServer(), "Query test");

    logger_.info("All tests finished");

    dispatcherInQueue_.add(new KillDispatcherMessage());
    finishPeer();
  }

  private static void messagesTests() {
    int count = 15;
    int maxPeers = 31;
    int minPeers = 15;
    int stepPeers = 5;

    int minEpoches = 10;
    int maxEpoches = 30;
    int stepEpoches = 2;

    int minMessagesInPhase = 10;
    int maxMessagesInPhase = 100;
    int stepMessagesInPhase = 10;

    for (int epoches = minEpoches; epoches < maxEpoches; epoches += stepEpoches) {
      for (int peers = minPeers; peers < maxPeers; peers += stepPeers) {
        if (peers == 15) {
          minMessagesInPhase = 60;
        } else {
          minMessagesInPhase = 10;
        }
        for (int messagesInPhase = minMessagesInPhase; messagesInPhase < maxMessagesInPhase; messagesInPhase += stepMessagesInPhase) {


          int i = 0;
          int succ = 0;
          while (succ < count) {
            String messagesDesc = "Messages test [" + epoches + "\t" + peers + "\t" +
                messagesInPhase + "] - " + "count: " + i;
            succ += runTest(new MessagesTestServer(epoches, 32, peers, 200,
                messagesDesc, messagesInPhase), "MessagesTestServer " + messagesDesc + "\t" +
                    i) ? 1 : 0;
            logger_
            .info("Finished Messages test. Moving to the next test.");
            i += 1;
          }
        }
      }
    }

  }

  private static void dhtTests() {
    int count = 10;

    int peersFound = 17;

    int maxPeers = 15;
    int minPeers = 14;
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
        if (peers == 5) {
          minKeysMultiplier = 10;
        } else {
          minKeysMultiplier = 2;
        }
         */
        /*
        for (int keysMultiplier = minKeysMultiplier; keysMultiplier < maxKeysMultiplier; keysMultiplier += stepKeysMultiplier) {

          int i = 0;
          int succ = 0;
          while (succ < count) {
            String bdbDesc = "Bdb test [" + epoches + "\t" + peers + "\t" +
                keysMultiplier + "] - count: " + i;
            succ += runTest(new BdbDHTTestServer(epoches, peers + 2, peers,
                keysMultiplier, bdbDesc), "BdbDHTTestServer " + bdbDesc + "\t" +
                    i) ? 1 : 0;
            i += 1;
          }
        }

         */
        minKeysMultiplier = 8;
        logger_
        .info("Finished Bdb DHT Test, performing Kademlia DHT Test...");
        for (int keysMultiplier = minKeysMultiplier; keysMultiplier < maxKeysMultiplier; keysMultiplier += stepKeysMultiplier) {
          int i = 0;
          int succ = 0;
          while (succ < count) {
            String kadDesc = "Kademlia test [" + epoches + "\t" + peers + "\t" +
                keysMultiplier + "] - count: " + i;
            succ += runTest(new KademliaDHTTestServer(epoches, peers + 4, peers,
                keysMultiplier, kadDesc), "KademliaDHTTestServer " + kadDesc +
                "\t" + i) ? 1 : 0;
            i += 1;
          }
        }
      }
    }

  }

  private static boolean runTest(ServerTestingModule testModule, String testName) {
    try {
      testModule.runThroughDispatcher(dispatcherInQueue_, testName + " server");
      testModule.getResult();
      return true;
    } catch (NebuloException exception) {
      testModule.endJobModule();
      logger_.error("NebuloException at test " + testName + " : " +
          exception.getMessage());
      return false;
    }
  }

}
