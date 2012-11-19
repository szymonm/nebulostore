package org.nebulostore.communication.address;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Persistent addressing service.
 * Inits DHT database and handles discovering and updating my address in DHT.
 *
 * @author Grzegorz Milka
 */
public interface IPersistentAddressingPeer {
  void setBootstrapDHTPort(int port);
  void setDHTPort(int port);
  void setCommPort(int port);
  void setBootstrapServerAddress(String bootstrapServerAddress);
  void setMyCommAddress(CommAddress myCommAddress);

  void setUpAndRun() throws IOException;
  void destroy();
  ICommAddressResolver getResolver();
  InetSocketAddress getCurrentInetSocketAddress() throws IOException;
  void uploadCurrentInetSocketAddress() throws IOException;
  void uploadCurrentInetSocketAddress(InetSocketAddress address)
    throws IOException;
}

