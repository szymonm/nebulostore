package org.nebulostore.communication.bootstrap;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.nebulostore.communication.address.CommAddress;

/**
 * @author Grzegorz Milka
 */

public interface CommAddressResolver {
  public InetSocketAddress resolve(CommAddress commAddress) throws IOException;
  //NOTE-GM Assuming persistent CommAddress so no setter
  public CommAddress getMyCommAddress();
}
