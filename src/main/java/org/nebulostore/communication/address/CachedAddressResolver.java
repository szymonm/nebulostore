package org.nebulostore.communication.address;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.nebulostore.communication.exceptions.AddressNotPresentException;

/**
 * Decorator adding caching of addresses.
 * @author Grzegorz Milka
 */
class CachedAddressResolver implements CommAddressResolver {
  // 60 seconds
  private static final long CACHE_TIMEOUT = 6000000;
  private static Logger logger_ = Logger.getLogger(CommAddressResolver.class);
  private Map<CommAddress, InetSocketAddress> cache_ =
    Collections.synchronizedMap(new HashMap<CommAddress, InetSocketAddress>());
  private Timer timer_ = new Timer(true);
  private CommAddressResolver resolver_;

  public CachedAddressResolver(CommAddressResolver resolver) {
    resolver_ = resolver;
  }

  @Override
  public InetSocketAddress resolve(CommAddress commAddress)
    throws IOException, AddressNotPresentException {
    InetSocketAddress inetSocketAddress = cache_.get(commAddress);
    if (inetSocketAddress == null) {
      logger_.trace("CommAddress: " + commAddress +
          " not present. Putting into cache.");
      inetSocketAddress = resolver_.resolve(commAddress);
      cache_.put(commAddress, inetSocketAddress);
      timer_.schedule(new CacheCleaner(commAddress), CACHE_TIMEOUT);
    }
    return inetSocketAddress;
  }

  public void reportFailure(CommAddress commAddress) {
    clearCache(commAddress);
  }

  @Override
  public CommAddress getMyCommAddress() {
    return resolver_.getMyCommAddress();
  }

  @Override
  public String toString() {
    return "CachedAddressResolver decorating: " + resolver_;
  }

  public void clearCache() {
    cache_.clear();
  }

  public void clearCache(CommAddress commAddress) {
    cache_.remove(commAddress);
  }

  /**
   * Cleans address from cache after specified time if it's still present.
   */
  private class CacheCleaner extends TimerTask {
    /**
     * Address to delete.
     */
    private CommAddress commAddress_;

    public CacheCleaner(CommAddress commAddress) {
      commAddress_ = commAddress;
    }

    @Override
    public void run() {
      cache_.remove(commAddress_);
    }
  }
}
