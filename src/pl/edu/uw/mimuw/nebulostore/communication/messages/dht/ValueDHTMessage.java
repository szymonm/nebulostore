package pl.edu.uw.mimuw.nebulostore.communication.messages.dht;

import pl.edu.uw.mimuw.nebulostore.communication.dht.KeyDHT;
import pl.edu.uw.mimuw.nebulostore.communication.dht.ValueDHT;
import pl.edu.uw.mimuw.nebulostore.communication.messages.CommMessage;

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
