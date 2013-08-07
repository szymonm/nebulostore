package org.nebulostore.systest.readwrite;

import org.nebulostore.communication.address.CommAddress;

import java.util.List;

/**
 * @author: hryciukrafal
 */
public class ReadWriteClientFactoryDefaultImpl implements ReadWriteClientFactory {

  @Override
  public ReadWriteClient createReadWriteClient(String serverJobId, CommAddress serverAddress, int numPhases, List<CommAddress> clients, int clientId) {
    return new ReadWriteClient(serverJobId, serverAddress, numPhases, clients, clientId);
  }

}
