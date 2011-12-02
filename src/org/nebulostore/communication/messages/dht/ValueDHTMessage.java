package org.nebulostore.communication.messages.dht;

import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.dht.ValueDHT;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author marcin
 */
public class ValueDHTMessage extends CommMessage {

  private final KeyDHT key_;
  private final ValueDHT value_;

  public ValueDHTMessage(KeyDHT key, ValueDHT value) {
    super(null, null);
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
