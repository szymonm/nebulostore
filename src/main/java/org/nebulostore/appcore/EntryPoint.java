package org.nebulostore.appcore;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * NebuloStore entry point.
 * Reads peer class name from configuration file, creates and runs it.
 * @author Bolek Kulbabinski
 */
public final class EntryPoint {
  private static Logger logger_ = Logger.getLogger(EntryPoint.class);
  private static final String LOG4J_CONFIG_PATH = "resources/conf/log4j.xml";
  private static final String CONFIGURATION_PATH = "resources/conf/Peer.xml";
  private static final String DEFAULT_PEER_CLASS = "org.nebulostore.appcore.Peer";

  public static void main(String[] args) {
    try {
      DOMConfigurator.configure(LOG4J_CONFIG_PATH);
      XMLConfiguration config = initConfig();
      setDefaultThreadUncaughtExceptionHandler();
      Peer peer = createPeer(config);
      Thread peerThread = new Thread(peer, "Peer Main Thread");
      peerThread.start();
      peerThread.join();
    } catch (NebuloException exception) {
      logger_.fatal("Unable to start NebuloStore! (" + exception.getMessage() + ")");
    } catch (InterruptedException e) {
      logger_.fatal("InterruptedException while waiting for peer thread!");
    }
  }

  /**
   * Default exception handler for threads.
   *
   * Logs error down and shuts down the application.
   * @author Grzegorz Milka
   */
  private static final class NebuloUncaughtExceptionHandler implements
    Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
      logger_.fatal("Thread: " + t + " has caught an irrecoverable " +
          "exception: " + e + ". Shutting down Nebulostore.");
      System.exit(1);
    }
  }

  private static void setDefaultThreadUncaughtExceptionHandler() {
    try {
      Thread.setDefaultUncaughtExceptionHandler(
          new NebuloUncaughtExceptionHandler());
    } catch (SecurityException e) {
      logger_.warn("Caught security exception: " + e +
          " when setting exception handler.");
    }
  }

  private static XMLConfiguration initConfig() throws NebuloException {
    try {
      return new XMLConfiguration(CONFIGURATION_PATH);
    } catch (ConfigurationException cex) {
      throw new NebuloException("Configuration read error in: " + CONFIGURATION_PATH);
    }
  }

  private static Peer createPeer(XMLConfiguration config) throws NebuloException {
    String className = config.getString("class-name", DEFAULT_PEER_CLASS);
    try {
      Peer peer = (Peer) Class.forName(className).newInstance();
      peer.setConfiguration(config);
      return peer;
    } catch (InstantiationException e) {
      throw new NebuloException("Could not instantiate class " + className + ".");
    } catch (IllegalAccessException e) {
      throw new NebuloException("Constructor for class " + className + " is not accessible.");
    } catch (ClassNotFoundException e) {
      throw new NebuloException("Class " + className + " not found.");
    }
  }

  private EntryPoint() { }
}
