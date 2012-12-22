package org.nebulostore.testing;

import java.rmi.RemoteException;
import java.util.Collection;

/**
 * @author grzegorzmilka
 */
public interface PingPongPeer extends AbstractPeer {
  /**
   * Returns Collection of respondents to ping of given id.
   */
  Collection<Integer> getRespondents(int pingId) throws RemoteException;
  void sendPing(int pingId) throws RemoteException;
}
