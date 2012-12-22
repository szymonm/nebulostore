package org.nebulostore.communication.address;

import java.io.IOException;
import java.net.InetSocketAddress;

import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import org.apache.log4j.Logger;
import org.nebulostore.communication.exceptions.AddressNotPresentException;

/**
 * Resolver in TomP2PPeer's enviroment.
 *
 * @author Grzegorz Milka
 */
class HashAddressResolver implements CommAddressResolver {
  private static Logger logger_ = Logger.getLogger(HashAddressResolver.class);
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
      futureDHT = myPeer_.get(new Number160(commAddress.hashCode())).start();
      logger_.trace("Returned FutureDHT: " + futureDHT);
      futureDHT.awaitUninterruptibly();
      Data data = futureDHT.getData();
      logger_.trace("Returned Data: " + data + " " + futureDHT.isCompleted() +
          " " + futureDHT.isSuccess());
      if (data == null)
        throw new AddressNotPresentException("Address not available in DHT.");
      InetSocketAddress inetSocketAddress = (InetSocketAddress) data.getObject();
      logger_.trace("Resolved " + commAddress + " to: " + inetSocketAddress + ".");
      return inetSocketAddress;
    } catch (IOException e) {
      String errMessage = "IOException when try to resolve address.";
      logger_.error(errMessage + " " + e);
      throw new IOException(errMessage, e);
    } catch (ClassNotFoundException e) {
      String errMessage = "Received data of unknown type given right key. " +
        "Expected InetSocketAddress.";
      logger_.error(errMessage);
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
