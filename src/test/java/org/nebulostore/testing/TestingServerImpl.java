package org.nebulostore.testing;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;

/**
 * @author grzegorzmilka
 */
public abstract class TestingServerImpl implements TestingServer, Runnable {
  private static final String PEER_REMOTE_NAME = "Peer";
  protected Logger logger_;

  public TestingServerImpl() {
    logger_ = Logger.getLogger(this.getClass());
  }

  @Override
  public abstract void run();

  /**
   * Registers given remote peer.
   *
   * Returns True if given peer has been successfully registered. It might
   * result in false if for example server is not longer accepting any peers.
   */
  // Synchronized just in case
  public final synchronized boolean registerClient(String clientAddress)
    throws RemoteException {
    // get Peer stub from client
    AbstractPeer peer;
    try {
      Registry registry = LocateRegistry.getRegistry(clientAddress);
      peer = (AbstractPeer) registry.lookup(PEER_REMOTE_NAME);
    } catch (NotBoundException e) {
      throw new RemoteException(
          "Client has not put up peer object named: " + PEER_REMOTE_NAME, e);
    } catch (RemoteException e) {
      throw new RemoteException(
          "RemoteException when trying to get peer.", e);
    }

    // add peer to local data
    try {
      return addPeer(peer);
    } catch (RemoteException e) {
      throw e;
    } catch (IllegalArgumentException e) {
      throw new RemoteException("Could not add peer", e);
    }
  }

  /**
   * Returns true if peer has been added successfully.
   */
  protected abstract boolean addPeer(AbstractPeer peer)
    throws RemoteException, IllegalArgumentException;
}
