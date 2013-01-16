package org.nebulostore.appcore;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * NebuloStore entry point. Reads configuration and runs appropriate peer.
 * @author bolek
 */
public final class EntryPoint {
  private static Logger logger_ = Logger.getLogger(EntryPoint.class);
  private static final String CONFIGURATION_PATH = "resources/conf/Peer.xml";
  private static final String LOG4J_PATH = "resources/conf/log4j.xml";
  private static final String DEFAULT_PEER_CLASS = "Peer";

  public static void main(String[] args) throws NebuloException {
    DOMConfigurator.configure(LOG4J_PATH);
    XMLConfiguration config = null;

    try {
      config = new XMLConfiguration(CONFIGURATION_PATH);
    } catch (ConfigurationException cex) {
      fatal("Configuration read error in: " + CONFIGURATION_PATH);
    }

    String className = config.getString("class-name", DEFAULT_PEER_CLASS);
    Peer peer = null;
    try {
      peer = (Peer) Class.forName(className).newInstance();
    } catch (InstantiationException e) {
      fatal("Could not instantiate class " + className + ".");
    } catch (IllegalAccessException e) {
      fatal("Constructor for class " + className + " is not accessible.");
    } catch (ClassNotFoundException e) {
      fatal("Class " + className + " not found.");
    }

    peer.setConfiguration(config);
    Thread peerThread = new Thread(peer, "Peer Main Thread");
    peerThread.start();
    try {
      peerThread.join();
    } catch (InterruptedException e) {
      fatal("InterruptedException while waiting for peer thread.");
    }
  }

  private static void fatal(String message) throws NebuloException {
    logger_.fatal(message);
    throw new NebuloException(message);
  }

  private EntryPoint() { }
}
