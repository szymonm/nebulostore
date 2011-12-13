package org.nebulostore.communication.dht;

import java.io.Serializable;

/**
 * @author marcin
 */
public class KeyDHT implements Serializable {

  private final String key_;

  public KeyDHT(String key) {
    key_ = key;
  }

  @Override
  public String toString() {
    return key_;
  }
}
