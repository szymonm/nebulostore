package org.nebulostore.appcore;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;
import org.nebulostore.testing.ServerTestingModule;
import org.nebulostore.testing.pingpong.PingPongServer;

/**
 * Class to run test server.
 * @author szymonmatejczyk
 *
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
      Thread.sleep(5000);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
      System.exit(-1);
    }

    // Insert test modules you want to be ran below.
    runTest(PingPongServer.class, "PingPong");

    dispatcherInQueue_.add(new KillDispatcherMessage());
    finishPeer();
  }

  private static void runTest(Class<? extends ServerTestingModule> moduleClass, String testName) {
    ServerTestingModule testModule;

    try {
      testModule = moduleClass.newInstance();
      testModule.runThroughDispatcher(dispatcherInQueue_, testName + " server");

      testModule.getResult();
    } catch (InstantiationException exception) {
      exception.printStackTrace();
      System.exit(-1);
    } catch (IllegalAccessException exception) {
      exception.printStackTrace();
      System.exit(-1);
    } catch (NebuloException exception) {
      logger_.warn("NebuloException: " + exception.getMessage());
      System.exit(-1);
    }
  }

}
