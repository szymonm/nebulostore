package org.nebulostore.communication.address;

import java.io.IOException;
import java.net.InetSocketAddress;

import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import org.apache.log4j.Logger;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.exceptions.AddressNotPresentException;

/**
 * Resolver in TomP2PPeer's enviroment.
 *
 * @author Grzegorz Milka
 */
class HashAddressResolver implements CommAddressResolver {
  private static Logger logger_ = Logger.getLogger(HashAddressResolver.class);
  private static final int MAX_RETRIES = 50;
  private final Peer myPeer_;
  private final CommAddress myCommAddress_;

  public HashAddressResolver(CommAddress myCommAddress, Peer myPeer) {
    myCommAddress_ = myCommAddress;
    myPeer_ = myPeer;
    logger_.trace("Created resolver: " + this + ".");
  }

  public InetSocketAddress resolve(CommAddress commAddress)
    throws IOException, AddressNotPresentException {
    FutureDHT futureDHT = null;
    try {
      logger_.trace("About to resolve: " + commAddress + ".");
      Data data = null;
      // TODO(grzegorzmilka): Fix this workaround.
      for (int i = 1; i <= MAX_RETRIES; ++i) {
        Number160 addressKey = KeyDHT.combine(
            KeyDHT.COMMUNICATION_KEY,
            new Number160(commAddress.hashCode()));
        futureDHT = myPeer_.get(addressKey).start();
        logger_.trace("Returned FutureDHT: " + futureDHT);
        futureDHT.awaitUninterruptibly();
        data = futureDHT.getData();
        if (data == null) {
          logger_.debug("NULL in HashAddressResolver in iteration " + i);
          try {
            Thread.sleep(300);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        } else {
          break;
        }
      }
      if (data == null)
        throw new AddressNotPresentException("Address not available in DHT.");
      logger_.trace("Returned Data: " + data + " " + futureDHT.isCompleted() +
          " " + futureDHT.isSuccess());
      InetSocketAddress inetSocketAddress = (InetSocketAddress) data.getObject();
      logger_.trace("Resolved " + commAddress + " to: " + inetSocketAddress + ".");
      return inetSocketAddress;
    } catch (IOException e) {
      String errMessage = "IOException when try to resolve address.";
      logger_.warn(errMessage + " " + e);
      throw new IOException(errMessage, e);
    } catch (ClassNotFoundException e) {
      String errMessage = "Received data of unknown type given right key. " +
        "Expected InetSocketAddress.";
      logger_.warn(errMessage);
      throw new IOException(errMessage, e);
    }
  }

  public CommAddress getMyCommAddress() {
    return myCommAddress_;
  }

  @Override
  public String toString() {
    return "HashAddressResolver at address: " + myCommAddress_ + " for peer: " +
      myPeer_;
  }
}
