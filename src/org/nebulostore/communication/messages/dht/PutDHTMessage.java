package org.nebulostore.communication.messages.dht;

import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.dht.ValueDHT;

/**
 * @author Marcin Walas
 */
public class PutDHTMessage extends InDHTMessage {
  private final KeyDHT key_;
  private final ValueDHT value_;

  public PutDHTMessage(KeyDHT key, ValueDHT value) {
    key_ = key;
    value_ = value;
  }

  public KeyDHT getKey() {
    return key_;
  }

  public ValueDHT getValue() {
    return value_;
  }
}
