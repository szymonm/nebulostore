package org.nebulostore.newcommunication.naming;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.exceptions.AddressNotPresentException;

/**
 * Resolves CommAddress to InetSocketAddress.
 *
 * @author Grzegorz Milka
 *
 */
public interface CommAddressResolver {
  /**
   * @param commAddress
   * @return Resolved {@link CommAddress}
   */
  InetSocketAddress resolve(CommAddress commAddress) throws AddressNotPresentException, IOException;

  /**
   * Notifies resolver that an error occurred when trying to contact last returned address to
   * this {@link CommAddress}, perhaps because resolution was erroneous.
   *
   * @param commAddress
   */
  void reportFailure(CommAddress commAddress);

  /**
   * Stops this resolver.
   */
  void shutDown();
}
