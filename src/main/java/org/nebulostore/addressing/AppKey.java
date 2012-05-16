package org.nebulostore.addressing;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Application Key.
 */
public class AppKey implements Serializable {
  private static final long serialVersionUID = -5977296486784377545L;
  private BigInteger key_;

  public AppKey(BigInteger key) {
    key_ = key;
  }

  public BigInteger getKey() {
    return key_;
  }

  public void setKey(BigInteger key) {
    key_ = key;
  }

  @Override
  public int hashCode() {
    return key_.hashCode();
  }

  @Override
  public String toString() {
    return "AppKey [" + key_.toString() + "]";
  }
}
