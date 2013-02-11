package org.nebulostore.systest.communication.pingpong;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Peer;
import org.nebulostore.appcore.exceptions.NebuloException;

/*TODO(grzegorzmilka) Add proper shutdown. Right now it's only a make-shift for
 * testing proper shutdown of CommunicationPeer. That is it only shutdowns
 * properly when running as a server. */
/**
 * Executes given PingPongPeer and if it is set as server it tries
 * to ping everyone and get responses.
 * @author Grzegorz Milka
 */
public final class RunPingPong extends Peer {
  private static Logger logger_ = Logger.getLogger(RunPingPong.class);
  /**
   * Allow testing server to start.
   */
  private static final int RMI_SERVER_BOOTSTRAP_PERIOD = 20000;

  private static final String PEER_REMOTE_NAME = "Peer";
  private static final String SERVER_REMOTE_NAME = "Server";
  /**
   * Prefix of test related parameters used in Peer.xml configuration file.
   */
  private static final String CONFIG_PREFIX = "systest.communication.pingpong.";

  private Thread serverThread_;
  private TestingServerImpl testingServer_;

  /**
   * Main function for registaring RMI objects and initializing them.
   * Needs pingpong-test-function to be set in config_ and in case of client
   * also: peer-id, server-net-address, peer-net-address
   */
  @Override
  protected void runPeer() {
    Thread.setDefaultUncaughtExceptionHandler(null);

    String function = config_.getString(CONFIG_PREFIX + "pingpong-test-function");
    boolean isServer = function.equals("server");

    try {
      if (isServer) {
        initializeServer();
        try {
          serverThread_.join();
        } catch (InterruptedException e) {
          logger_.warn("Caught interrupt while joining to TestingServer");
        }
        if (!Thread.currentThread().isInterrupted()) {
          shutdownServer();
        }
        System.exit(0);
      } else {
        logger_.info("Creating client.");
        try {
          initializeClient();
        } catch (NebuloException e) {
          logger_.error("Client caught NebuloException: " + e);
        }
      }
    } catch (RemoteException e) {
      logger_.error("RunPingPong caught RemoteException: " + e);
      System.exit(1);
    }
  }

  /**
   * Initializes TestingServer (PingPongServer) and returns thread running it.
   */
  private void initializeServer() throws RemoteException {
    logger_.info("Creating server.");
    try {
      testingServer_ = new PingPongServer();
    } catch (NebuloException e) {
      logger_.error("Caught exception when creating peer: " + e);
      return;
    }
    try {
      Registry localRegistry = LocateRegistry.createRegistry(1099);
      logger_.info("Local registry created");
      TestingServer stub =
        (TestingServer) UnicastRemoteObject.exportObject(testingServer_, 0);
      localRegistry.rebind(SERVER_REMOTE_NAME, stub);
      logger_.info("Server: " + testingServer_ + " has been put to remote.");
    } catch (RemoteException e) {
      logger_.error("Received exception: " + e + ", ending client.");
      throw e;
    }
    serverThread_ = new Thread(testingServer_, "Org.Nebulostore.Testing.TestingServer");
    serverThread_.start();
  }

  private void shutdownServer() throws RemoteException {
    UnicastRemoteObject.unexportObject(testingServer_, false);
  }

  private void initializeClient() throws NebuloException, RemoteException {
    String peerIdStr = config_.getString(CONFIG_PREFIX + "peer-id", "");

    if (peerIdStr.isEmpty()) {
      throw new IllegalArgumentException("PeerId needs to be set");
    }
    int peerId;
    try {
      peerId = Integer.parseInt(peerIdStr);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("PeerId needs to be an integer");
    }

    String myAddress = config_.getString(CONFIG_PREFIX + "peer-net-address", "");
    String serverAddress = config_.getString(CONFIG_PREFIX + "server-net-address", "");
    if (myAddress.isEmpty() || serverAddress.isEmpty()) {
      throw new IllegalArgumentException(
          "Peer or server address or peer id needs to be set");
    }

    PingPongPeerImpl pingPongPeer;
    try {
      pingPongPeer = new PingPongPeerImpl(peerId);
    } catch (NebuloException e) {
      logger_.error("Caught NebuloException when creating peer");
      throw e;
    }
    String peerName = PEER_REMOTE_NAME;
    String serverName = SERVER_REMOTE_NAME;
    assert serverName != null && peerName != null;
    Registry localRegistry;
    // Put my Peer to remote.
    try {
      PingPongPeer stub =
        (PingPongPeer) UnicastRemoteObject.exportObject(pingPongPeer, 0);
      localRegistry = LocateRegistry.createRegistry(1099);
      logger_.info("Local registry created");
      localRegistry.rebind(peerName, stub);
      logger_.info("Peer: " + pingPongPeer + " has been put to remote.");
    } catch (RemoteException e) {
      logger_.error("Received exception: " + e + ", ending client.");
      return;
    }

    /* Connect to server and register. Unbind peer in case of failure
     * Wait for server to start */
    try {
      Thread.sleep(RMI_SERVER_BOOTSTRAP_PERIOD);
    } catch (InterruptedException e) {
      logger_.debug("Ignored InterruptedException: " + e);
    }
    try {
      Registry registry = LocateRegistry.getRegistry(serverAddress);
      TestingServer server = (TestingServer) registry.lookup(serverName);
      server.registerClient(myAddress);
      logger_.info("Registered myself(" + myAddress + ") at server");
    } catch (NotBoundException e) {
      logger_.error("Received exception: " + e + ", ending client.");
      return;
    } catch (RemoteException e) {
      logger_.error("Received exception: " + e + ", ending client.");
      return;
    } finally {
      try {
        localRegistry.unbind(peerName);
      } catch (NotBoundException e) {
        logger_.error("Peer not bound. This can not happen. Ending.");
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        logger_.error(stringWriter.toString());
        return;
      } catch (RemoteException e) {
        logger_.error("Received exception: " + e + " ending client.");
        return;
      }
    }
  }
}
