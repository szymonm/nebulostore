package org.nebulostore.systest.communication.pingpong;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author grzegorzmilka
 */
public interface TestingServer extends Remote {
  /**
   * Registers given remote peer.
   *
   * Returns True if given peer has been successfully registered. It might
   * result in false if for example server is not longer accepting any peers.
   */
  // Synchronized just in case
  boolean registerClient(String clientAddress) throws RemoteException;
}
