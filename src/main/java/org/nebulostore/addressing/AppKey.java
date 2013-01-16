package org.nebulostore.addressing;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Application Key, identifies the user.
 * (immutable)
 */
public final class AppKey implements Serializable {
  private static final long serialVersionUID = -5977296486784377545L;
  private final BigInteger key_;

  public AppKey(BigInteger key) {
    key_ = key;
  }

  public BigInteger getKey() {
    return key_;
  }

  @Override
  public int hashCode() {
    return key_.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o != null && (o instanceof AppKey) && (((AppKey) o).key_.equals(key_))) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "AppKey[" + key_.toString() + "]";
  }
}
