package org.nebulostore.systest.communication.pingpong;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
  private PingPongPeerImpl pingPongPeer_;

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
      } else {
        logger_.info("Creating client.");
        boolean result = false;
        try {
          result = initializeClient();
        } catch (NebuloException e) {
          logger_.error("Client caught NebuloException: " + e);
        } finally {
          if (!result) {
            cleanUpClient();
          }
        }
      }
    } catch (RemoteException e) {
      logger_.error("RunPingPong caught RemoteException: " + e);
    }
  }

  /**
   * Initializes TestingServer (PingPongServer) and returns thread running it.
   */
  private void initializeServer() throws RemoteException {
    logger_.info("Creating server.");
    try {
      testingServer_ = new PingPongServer(commAddress_);
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

  /**
   * Returns true iff successful.
   */
  private boolean initializeClient() throws NebuloException, RemoteException {
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

    try {
      pingPongPeer_ = new PingPongPeerImpl(peerId, commAddress_);
    } catch (NebuloException e) {
      logger_.error("Caught NebuloException when creating peer");
      throw e;
    }

    /* Observer of pingPongPeer for closing of communication */
    PeerObserver peerObserver = new PeerObserver();
    Thread peerObserverThread = new Thread(peerObserver,
        "org.nebulostore.systest.communication.pingpong.PeerObserver");
    peerObserverThread.setDaemon(true);
    peerObserverThread.start();
    pingPongPeer_.addObserver(peerObserver);

    String peerName = PEER_REMOTE_NAME;
    String serverName = SERVER_REMOTE_NAME;
    Registry localRegistry;
    // Put my Peer to remote.
    try {
      PingPongPeer stub =
        (PingPongPeer) UnicastRemoteObject.exportObject(pingPongPeer_, 0);
      localRegistry = LocateRegistry.createRegistry(1099);
      logger_.info("Local registry created");
      localRegistry.rebind(peerName, stub);
      logger_.info("Peer: " + pingPongPeer_ + " has been put to remote.");
    } catch (RemoteException e) {
      logger_.error("Received exception: " + e + ", ending client.");
      return false;
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
      return false;
    } catch (RemoteException e) {
      logger_.error("Received exception: " + e + ", ending client.");
      return false;
    } finally {
      try {
        localRegistry.unbind(peerName);
      } catch (NotBoundException e) {
        logger_.error("Peer not bound. This can not happen. Ending.");
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        logger_.error(stringWriter.toString());
        return false;
      } catch (RemoteException e) {
        logger_.error("Received exception: " + e + " ending client.");
        return false;
      }
    }
    return true;
  }

  public void cleanUpClient() {
    try {
      if (pingPongPeer_ != null)
        UnicastRemoteObject.unexportObject(pingPongPeer_, true);
    } catch (NoSuchObjectException e) {
      logger_.warn("Couldn't export peer: " + e);
    }
  }

  /**
   * Waits for notification from AbstractPeer whether it has been stopped.
   * If so it waits till it is no longer used and unexports it.
   *
   * This test uses Java RMI to conduct tests. RMI runs a non-daemon thread (RMI
   * Reaper which handles exported objects. In-order to shutdown JVM only daemon
   * threads can be present. To shutdown RMI one needs to unexport all exported
   * objects.
   * This observer unexports the only object client exports (pingPongPeer_) when
   * it has been shutdown. It waits for AbstractPeer's notification that it is
   * closing itself (using observer pattern) and then unexports it when it is no
   * longer active. Satisfying the condition that on shutdown no objects can be
   * exported for RMI.
   */
  private class PeerObserver implements Observer, Runnable {
    private final ReentrantLock lock_ = new ReentrantLock();
    private final Condition notified_ = lock_.newCondition();
    private boolean wasNotified_;

    @Override
    public void update(Observable o, Object arg) {
      assert o.equals(pingPongPeer_);
      try {
        lock_.lock();
        wasNotified_ = true;
        notified_.signal();
      } finally {
        lock_.unlock();
      }
    }

    @Override
    public void run() {
      try {
        lock_.lock();
        while (!wasNotified_)
          notified_.await();
        lock_.unlock();
        while (!Thread.interrupted() && pingPongPeer_.isActive()) {
          /* pingPongPeer_ is shutting down. Wait till it is finished. */
          logger_.debug("PeerObserver sleeping waiting for shutdown of peer.");
          Thread.sleep(1000);
        }
      } catch (InterruptedException e) {
        logger_.debug("PeerObserver received interrupt. Leaving the loop.");
        Thread.currentThread().interrupt();
      } catch (RemoteException e) {
        logger_.error("Remote Exception during call to local peer. " +
            "This SHOULDN'T happen. " + e);
      } finally {
        if (lock_.isHeldByCurrentThread())
          lock_.unlock();
      }
      logger_.debug("Unexporting Peer.");
      cleanUpClient();
    }
  }
}
