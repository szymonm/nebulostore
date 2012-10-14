package org.nebulostore.testing;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;

public interface IAbstractPeer extends Remote {
  int getId() throws RemoteException;
  void startCommPeer() throws NebuloException, RemoteException;
  void stopCommPeer() throws RemoteException;
  CommAddress getCommAddress() throws RemoteException;
  /**
   * Is CommunicationPeer active?
   */
  boolean isActive() throws RemoteException;
}
