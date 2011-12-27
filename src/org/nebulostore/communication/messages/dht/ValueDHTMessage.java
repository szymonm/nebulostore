package org.nebulostore.communication.messages.dht;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.dht.ValueDHT;

/**
 * @author marcin
 */
public class ValueDHTMessage extends OutDHTMessage {

  private final KeyDHT key_;
  private final ValueDHT value_;

  public ValueDHTMessage(InDHTMessage reqMessage, KeyDHT key, ValueDHT value) {
    super(reqMessage);
    key_ = key;
    value_ = value;
  }

  public KeyDHT getKey() {
    return key_;
  }

  public ValueDHT getValue() {
    return value_;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
