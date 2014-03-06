package org.nebulostore.newcommunication.naming;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.google.inject.Inject;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.exceptions.AddressNotPresentException;
import org.nebulostore.newcommunication.naming.addressmap.AddressMap;
import org.nebulostore.newcommunication.naming.addressmap.AddressMapFactory;


/**
 * CommAddressResolver which uses address map to resolve given CommAddress.
 *
 * @author Grzegorz Milka
 *
 */
public class SimpleCommAddressResolver implements CommAddressResolver {
  private AddressMap addressMap_;
  private final AddressMapFactory addressMapFactory_;

  @Inject
  public SimpleCommAddressResolver(AddressMapFactory addressMapFactory) {
    addressMapFactory_ = addressMapFactory;
  }

  @Override
  public InetSocketAddress resolve(CommAddress commAddress) throws AddressNotPresentException,
      IOException {
    if (addressMap_ == null) {
      synchronized (addressMapFactory_) {
        if (addressMap_ == null) {
          addressMap_ = addressMapFactory_.getAddressMap();
        }
      }
    }

    InetSocketAddress netAddress = addressMap_.getAddress(commAddress);
    if (netAddress == null) {
      throw new AddressNotPresentException("CommAddress: " + commAddress + " is not present.");
    }
    return netAddress;
  }

  @Override
  public void reportFailure(CommAddress commAddress) {
    /* Do nothing */
  }

  @Override
  public void shutDown() {
    /* Do nothing */
  }
}
