package org.nebulostore.conductor;

import java.math.BigInteger;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.api.ApiFacade;
import org.nebulostore.appcore.Peer;
import org.nebulostore.appcore.context.NebuloContext;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.dht.BdbDHTTestServer;
import org.nebulostore.communication.dht.KademliaDHTTestServer;
import org.nebulostore.communication.messages.MessagesTestServer;
import org.nebulostore.communication.messages.performance.PerformanceMessagesTestServer;
import org.nebulostore.conductor.pingpong.PingPongServer;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;

/**
 * Class to run test server.
 * @author szymonmatejczyk
 */
public final class TestingPeer extends Peer {
  private static Logger logger_ = Logger.getLogger(TestingPeer.class);
  private static Injector injector_ = Guice.createInjector(new NebuloContext());
  private static final int INITIAL_DELAY_SEC = 30;

  private TestingPeer() {
  }

  public static void main(String[] args) {
    DOMConfigurator.configure("resources/conf/log4j.xml");
    BigInteger appKey = BigInteger.ZERO;
    boolean success = true;
    if (args.length < 1) {
      // Random AppKey if not provided.
      appKey = CryptoUtils.getRandomId();
      logger_.debug("Random appKey generateds = " + appKey);
    } else {
      appKey = new BigInteger(args[0]);
    }
    logger_.info("Starting testing peer with appKey = " + appKey);
    startPeer(new AppKey(appKey), injector_);

    try {
      ApiFacade.putKey(new AppKey(appKey));
    } catch (NebuloException e) {
      logger_.error(e);
    }

    // Insert test modules you want to be ran below.
    logger_.info("Starting ping-pong test.");
    if (runTest(new PingPongServer(), "PingPong")) {
      success = true;
      logger_.info("Test succeeded!");
    } else {
      success = false;
      logger_.info("Test failed!");
    }
    // logger_.info("Finished PingPong test, performing DHT tests...");

    // dhtTests();

    // logger_.info("Finished DHT tests, performing messages tests...");

    // messagesTests();

    // messagesPerfTests();

    // runTest(new TrivialQueryTestServer(), "Query test");

    logger_.info("All tests finished");

    dispatcherInQueue_.add(new KillDispatcherMessage());
    finishPeer();
    System.exit(success ? 0 : 1);
  }

  private static void messagesPerfTests() {

    int count = 5;
    int maxPeers = 91;
    int minPeers = 5;
    int stepPeers = 10;

    int minEpoches = 10;
    int maxEpoches = 11;
    int stepEpoches = 2;

    int minMessagesInPhase = 1;
    int maxMessagesInPhase = 6;
    int stepMessagesInPhase = 1;

    // warmup
    int j = 0;
    int successful = 0;
    int epochesWarmup = 3;
    int messagesInPhaseWarmup = 5;

    for (int epoches = minEpoches; epoches < maxEpoches; epoches += stepEpoches) {
      for (int peers = minPeers; peers < maxPeers; peers += stepPeers) {

        successful = 0;
        while (successful < count) {
          String messagesDesc = "PerfMsg test [" + epochesWarmup + "\t" +
              peers + "\t" + messagesInPhaseWarmup + "] WARM " + "c: " + j;
          int toAdd = runTest(new PerformanceMessagesTestServer(peers + 10,
              peers, 380, messagesDesc, 5, 10, 1000), "MsgSrv " + messagesDesc +
              "\t" + j) ? 1 : 0;

          successful += toAdd;
          if (toAdd == 0) {
            logger_.info("Additionally sleeping after failed test");
            try {
              Thread.sleep(peers * 1000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          logger_
          .info("Finished warmup Messages test. Moving to the next test.");

          try {
            Thread.sleep(peers * 1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          j += 1;
        }
      }
    }
  }

  private static void messagesTests() {

    int count = 5;
    int maxPeers = 91;
    int minPeers = 35;
    int stepPeers = 10;

    int minEpoches = 10;
    int maxEpoches = 11;
    int stepEpoches = 2;

    int minMessagesInPhase = 1;
    int maxMessagesInPhase = 6;
    int stepMessagesInPhase = 1;

    // warmup
    int j = 0;
    int successful = 0;
    int epochesWarmup = 3;
    int messagesInPhaseWarmup = 5;

    for (int epoches = minEpoches; epoches < maxEpoches; epoches += stepEpoches) {
      for (int peers = minPeers; peers < maxPeers; peers += stepPeers) {

        successful = 0;
        while (successful < count) {
          String messagesDesc = "Messages test [" + epochesWarmup + "\t" +
              peers + "\t" + messagesInPhaseWarmup + "] WARM " + "c: " + j;
          int toAdd = runTest(new MessagesTestServer(epochesWarmup, peers + 10,
              peers, 380, messagesDesc, messagesInPhaseWarmup), "MsgSrv " +
                  messagesDesc + "\t" + j) ? 1 : 0;

          successful += toAdd;
          if (toAdd == 0) {
            logger_.info("Additionally sleeping after failed test");
            try {
              Thread.sleep(peers * 10 * 1000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          logger_
          .info("Finished warmup Messages test. Moving to the next test.");

          try {
            Thread.sleep(peers * 5 * 1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          j += 1;
        }

        for (int messagesInPhase = minMessagesInPhase; messagesInPhase < maxMessagesInPhase;
            messagesInPhase += stepMessagesInPhase) {

          int i = 0;
          int succ = 0;
          while (succ < count) {
            String messagesDesc = "Msg [" + epoches + "\t" + peers + "\t" +
                messagesInPhase + "] - " + "c: " + i;
            int toAdd = runTest(new MessagesTestServer(epoches, peers + 15,
                peers, (peers * messagesInPhase) + epoches * 25, messagesDesc,
                messagesInPhase), "MsgSrvr " + messagesDesc + "\t" + i) ? 1 : 0;
            logger_.info("Finished Messages test. Moving to the next test.");

            succ += toAdd;

            if (toAdd == 0) {
              logger_.info("Additionally sleeping after failed test");
              try {
                Thread.sleep(peers * 10 * 1000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }

            try {
              Thread.sleep(peers * 5 * 1000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }

            i += 1;
          }
          try {
            Thread.sleep(peers * 10 * 1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }

      try {
        Thread.sleep(600 * 1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

  private static void dhtTests() {
    int count = 5;

    int maxPeers = 81;
    int minPeers = 20;
    int stepPeers = 5;

    int minEpoches = 7;
    int maxEpoches = 8;
    int stepEpoches = 2;

    int minKeysMultiplier = 1;
    int maxKeysMultiplier = 6;
    int stepKeysMultiplier = 1;

    for (int epoches = minEpoches; epoches < maxEpoches; epoches += stepEpoches) {

      for (int peers = minPeers; peers < maxPeers; peers += stepPeers) {

        int keysMultiplierWarmup = 3;
        for (int i = 0; i < 3; i++) {
          String kadDesc = "Kademlia test [" + epoches + "\t" + peers + "\t" +
              keysMultiplierWarmup + "] - WARM count: " + i;
          int toAdd = runTest(new KademliaDHTTestServer(epoches, peers + 10,
              peers, keysMultiplierWarmup, kadDesc), "KademliaDHTTestServer " +
                  kadDesc + "\t" + i) ? 1 : 0;
        }

        logger_.info("Finished Bdb DHT Test, performing Kademlia DHT Test...");
        for (int keysMultiplier = minKeysMultiplier; keysMultiplier < maxKeysMultiplier;
        keysMultiplier += stepKeysMultiplier) {
          int i = 0;
          int succ = 0;
          while (succ < count) {
            String kadDesc = "Kademlia test [" + epoches + "\t" + peers + "\t" +
                keysMultiplier + "] - count: " + i;
            int toAdd = runTest(new KademliaDHTTestServer(epoches, peers + 10,
                peers, keysMultiplier, kadDesc), "KademliaDHTTestServer " +
                    kadDesc + "\t" + i) ? 1 : 0;
            succ += toAdd;
            if (toAdd == 0) {
              logger_.info("Additionally sleeping after failed test");
              try {
                Thread.sleep(peers * 3 * 1000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }

            try {
              Thread.sleep(peers * 1000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            i += 1;
          }
        }

      }

      for (int peers = minPeers; peers < maxPeers; peers += stepPeers) {
        int keysMultiplierWarmup = 3;
        for (int i = 0; i < 3; i++) {
          String kadDesc = "Bdb test [" + epoches + "\t" + peers + "\t" +
              keysMultiplierWarmup + "] - WARM count: " + i;
          int toAdd = runTest(new BdbDHTTestServer(epoches, peers + 10, peers,
              keysMultiplierWarmup, kadDesc), "BdbDHTTestServer " + kadDesc +
              "\t" + i) ? 1 : 0;
        }

        for (int keysMultiplier = minKeysMultiplier; keysMultiplier < maxKeysMultiplier;
            keysMultiplier += stepKeysMultiplier) {
          int i = 0;
          int succ = 0;
          while (succ < count) {
            String bdbDesc = "Bdb test [" + epoches + "\t" + peers + "\t" +
                keysMultiplier + "] - count: " + i;
            int toAdd = runTest(new BdbDHTTestServer(epoches, peers + 10,
                peers, keysMultiplier, bdbDesc), "BdbDHTTestServer " + bdbDesc +
                "\t" + i) ? 1 : 0;
            i += 1;

            succ += toAdd;

            if (toAdd == 0) {
              logger_.info("Additionally sleeping after failed test");
              try {
                Thread.sleep(peers * 1000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }

            try {
              Thread.sleep(peers * 1000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }

          }
        }
      }

    }

  }

  private static boolean runTest(ConductorServer testModule, String testName) {
    try {
      testModule.runThroughDispatcher(dispatcherInQueue_);
      testModule.getResult();
      return true;
    } catch (NebuloException exception) {
      logger_.error("NebuloException at test " + testName + " : " +
          exception.getMessage());
      return false;
    }
  }

}
