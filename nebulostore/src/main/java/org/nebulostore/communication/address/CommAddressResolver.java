package org.nebulostore.communication.address;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.nebulostore.communication.exceptions.AddressNotPresentException;

/**
 * Resolver from persitent address to network one.
 *
 * @author Grzegorz Milka
 */
public interface CommAddressResolver {
  InetSocketAddress resolve(CommAddress commAddress) throws IOException, AddressNotPresentException;

  /**
   * Function called by object using the resolver when given address seems
   * to be erroneous.
   *
   * @param commAddress
   */
  void reportFailure(CommAddress commAddress);

  //NOTE-GM Assuming persistent CommAddress so no setter
  CommAddress getMyCommAddress();
}
