package org.nebulostore.communication.socket;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.TreeMap;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.address.CommAddressResolver;
import org.nebulostore.communication.exceptions.AddressNotPresentException;

/**
 * CommAddressResolver for tests. Manual stands for adding mappings from
 * CommAddress to net addresses manually.
 *
 * @author grzesiek
 *
 */
public class ManualResolver implements CommAddressResolver {
  private CommAddress myCommAddress_;
  private Map<CommAddress, InetSocketAddress> caToInetMap_;

  public ManualResolver(CommAddress myCommAddress) {
    myCommAddress_ = myCommAddress;
    caToInetMap_ = new TreeMap<>();
  }

  public void addMapping(CommAddress ca, InetSocketAddress isa) {
    caToInetMap_.put(ca, isa);
  }

  @Override
  public InetSocketAddress resolve(CommAddress commAddress) throws AddressNotPresentException {
    InetSocketAddress isa = caToInetMap_.get(commAddress);
    if (isa == null) {
      throw new AddressNotPresentException();
    }

    return isa;
  }

  @Override
  public void reportFailure(CommAddress commAddress) {
    /* Do nothing */
  }

  @Override
  public CommAddress getMyCommAddress() {
    return myCommAddress_;
  }

}
