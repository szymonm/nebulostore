package org.nebulostore.communication.address;

import java.io.Serializable;
import java.io.InetAddress;

/**
 * @author Marcin Walas
 * @author Grzegorz Milka
 */
public class CommAddress implements Serializable {
  private final InetAddress inetAddress_;

  public CommAddress(InetAddress inetAddress) {
    inetAddress_ = inetAddress;
  }

  public InetAddress getAddress() {
    return inetAddress_;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof CommAddress) && 
        (inetAddress_ == ((CommAddress) o).inetAddress_);
  }

  @Override
  public String toString() {
    return inetAddress_.toString();
  }
}
