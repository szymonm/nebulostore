package org.nebulostore.appcore;

import java.io.Serializable;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.KeyDHT;

/**
 * Application instance ID.
 * @author szymonmatejczyk
 *
 */
public class InstanceID implements Serializable, Comparable<InstanceID> {
  private static final long serialVersionUID = 77L;

  private final CommAddress address_;

  public InstanceID(CommAddress address) {
    address_ = address;
  }

  public CommAddress getAddress() {
    return address_;
  }

  @Override
  public String toString() {
    return "InstanceID: " + address_.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((address_ == null) ? 0 : address_.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    InstanceID other = (InstanceID) obj;
    if (address_ == null) {
      if (other.address_ != null)
        return false;
    } else if (!address_.equals(other.address_))
      return false;
    return true;
  }

  public KeyDHT toKeyDHT() {
    return null;
  }

  @Override
  public int compareTo(InstanceID other) {
    return address_.compareTo(other.address_);
  }
}
