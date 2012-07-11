package org.nebulostore.communication.address;

import java.io.Serializable;
import java.net.InetSocketAddress;
import net.jxta.peer.PeerID;

/**
 * @author Marcin Walas
 * @author Grzegorz Milka
 */
public class CommAddress implements Serializable {
  private final InetSocketAddress address_;

  public CommAddress(InetSocketAddress address) {
    address_ = address;
  }

  public InetSocketAddress getAddress() {
    return address_;
  }

  //TODO-GM Remove this function with the rest of jxta
  public InetSocketAddress getPeerId() {
    return address_;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof CommAddress) && 
        (address_ == ((CommAddress) o).address_);
  }

  @Override
  public String toString() {
    return address_.toString();
  }
}
