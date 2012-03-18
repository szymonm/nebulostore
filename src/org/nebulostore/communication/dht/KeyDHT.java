package org.nebulostore.communication.dht;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author marcin
 */
public class KeyDHT implements Serializable {

  private final BigInteger key_;

  public KeyDHT(BigInteger key) {
    key_ = key;
  }

  @Override
  public String toString() {
    return key_.toString();
  }

  public byte[] getBytes() {
    return key_.toByteArray();
  }
}
