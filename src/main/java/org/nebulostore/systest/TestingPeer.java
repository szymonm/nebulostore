package org.nebulostore.systest;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Peer;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.conductor.ConductorServer;

/**
 * Class that runs test server. If IS_SERVER_CONFIG option is set to false
 * it behaves like standard Peer.
 *
 * @author Bolek Kulbabinski
 */
public class TestingPeer extends Peer {
  private static Logger logger_ = Logger.getLogger(TestingPeer.class);
  private static final String CLASS_LIST_CONFIG = "systest.testing-peer-class-list";
  private static final String IS_SERVER_CONFIG = "systest.is-server";

  @Override
  protected void runPeer() {
    logger_.info("Starting testing peer with appKey = " + appKey_);
    startPeer();
    putKey();

    if (config_.getBoolean(IS_SERVER_CONFIG, false))
      runTestingServer();

    finishPeer();
    System.exit(0);
  }

  protected void runTestingServer() {
    String[] testClasses = config_.getStringArray(CLASS_LIST_CONFIG);
    logger_.info("Running " + testClasses.length + " tests.");
    for (String className : testClasses) {
      ConductorServer testServer = null;
      try {
        testServer = (ConductorServer) Class.forName(className).newInstance();
        testServer.initialize(config_);
        logger_.info("Starting " + className + " test.");
        if (runTest(testServer, className)) {
          logger_.info("Test " + className + " succeeded!");
        } else {
          fatal("Test " + className + " failed!");
        }
      } catch (InstantiationException e) {
        fatal("Could not instantiate class " + className + ".");
      } catch (IllegalAccessException e) {
        fatal("Constructor for class " + className + " is not accessible.");
      } catch (ClassNotFoundException e) {
        fatal("Class " + className + " not found.");
      }
    }
    logger_.info("All tests finished successfully.");
    quitNebuloStore();
  }

  protected void fatal(String message) {
    logger_.fatal(message);
    System.exit(1);
  }

  private boolean runTest(ConductorServer testModule, String testName) {
    try {
      testModule.runThroughDispatcher(dispatcherInQueue_);
      testModule.getResult();
      return true;
    } catch (NebuloException exception) {
      logger_.error("NebuloException at test " + testName + " : " + exception.getMessage());
      return false;
    }
  }
}
