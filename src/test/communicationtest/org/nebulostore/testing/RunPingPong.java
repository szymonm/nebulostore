package org.nebulostore.testing;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Executes given PingPongPeer and if it is set as server it tries
 * to ping everyone and get responses.
 * @author Grzegorz Milka
 */
public final class RunPingPong {
  // 10 seconds
  private static final int RMI_SERVER_BOOTSTRAP_PERIOD_ = 10000;

  private static final Random randGenerator = new Random();
  private final Map<Integer, PingPongPeer> otherPeers_ = null;

  private static final String PEER_REMOTE_NAME_ = "Peer";
  private static final String SERVER_REMOTE_NAME_ = "Server";
  private static Logger logger_;

  private RunPingPong() {
  }

  public static void main(String[] args) throws NebuloException, RemoteException {
    DOMConfigurator.configure("resources/conf/log4j.xml");
    logger_ = Logger.getLogger(RunPingPong.class);

    Thread.setDefaultUncaughtExceptionHandler(null);

    String usage = "Usage: program {server| PEER_ID!=0 MY_NAME SERVER_NAME client}";
    if (args.length != 1 && args.length != 4)
      throw new IllegalArgumentException(usage);
    else if ((args.length == 4 && ((!args[3].equals("client")) ||
                                   (args[0].equals("0")))) ||
             (args.length == 1 && !args[0].equals("server")))
      throw new IllegalArgumentException(usage);

    boolean isServer = args.length == 1;

    if (isServer) {
      logger_.info("Creating server.");
      TestingServer server;
      try {
        server = new PingPongServer();
      } catch (NebuloException e) {
        logger_.error("Caught exception when creating peer: " + e);
        return;
      }
      try {
        Registry localRegistry = LocateRegistry.createRegistry(1099);
        logger_.info("Local registry created");
        ITestingServer stub =
          (ITestingServer) UnicastRemoteObject.exportObject((ITestingServer) server, 0);
        localRegistry.rebind(SERVER_REMOTE_NAME_, stub);
        logger_.info("Server: " + server + " has been put to remote.");
      } catch (RemoteException e) {
        logger_.error("Received exception: " + e + ", ending client.");
        throw e;
      }
      Thread thread = new Thread(server, "Org.Nebulostore.Testing.TestingServer");
      thread.start();
    } else {
      logger_.info("Creating client.");
      int peerId = Integer.parseInt(args[0]);
      String myAddress = args[1];
      String serverAddress = args[2];
      initializeClient(peerId, myAddress, serverAddress);
    }
  }

  private static void initializeClient(int peerId, String myAddress,
      String serverAddress) throws NebuloException {
    assert myAddress != null && serverAddress != null;
    PingPongPeer pingPongPeer;
    try {
      pingPongPeer = new PingPongPeer(peerId);
    } catch (NebuloException e) {
      logger_.error("Caught NebuloException when creating peer");
      throw e;
    }
    String peerName = PEER_REMOTE_NAME_;
    String serverName = SERVER_REMOTE_NAME_;
    assert serverName != null && peerName != null;
    Registry localRegistry;
    // Put my Peer to remote.
    try {
      IPingPongPeer stub =
        (IPingPongPeer) UnicastRemoteObject.exportObject(pingPongPeer, 0);
      //LocateRegistry.getRegistry();
      localRegistry = LocateRegistry.createRegistry(1099);
      logger_.info("Local registry created");
      localRegistry.rebind(peerName, stub);
      logger_.info("Peer: " + pingPongPeer + " has been put to remote.");
    } catch (RemoteException e) {
      logger_.error("Received exception: " + e + ", ending client.");
      return;
    }

    // Connect to server and register
    // Unbind peer in case of failure
    // Wait for server to start
    try {
      Thread.sleep(RMI_SERVER_BOOTSTRAP_PERIOD_);
    } catch (InterruptedException e) {
      //ignore
    }
    try {
      Registry registry = LocateRegistry.getRegistry(serverAddress);
      ITestingServer server = (ITestingServer) registry.lookup(serverName);
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
