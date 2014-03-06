package org.nebulostore.communication.socket;

import java.io.IOException;
import java.io.ObjectOutputStream;

import org.nebulostore.communication.address.CommAddress;

/**
 * Handles creating and cleaning sockets and ObjectOutputStreams tied to them.
 *
 * @author Grzegorz Milka
 */
public interface OOSDispatcher {
  ObjectOutputStream get(CommAddress commAddress) throws IOException,
         InterruptedException;
  void put(CommAddress commAddress, ObjectOutputStream oos);
  void shutdown();
}
