package org.nebulostore.systest.readwrite;

import org.nebulostore.communication.address.CommAddress;

import java.util.List;

/**
 * @author hryciukrafal
 */
public interface ReadWriteClientFactory {

  ReadWriteClient createReadWriteClient(String serverJobId, CommAddress serverAddress, int numPhases,
                                        List<CommAddress> clients, int clientId);

}
