package org.nebulostore.communication.messages.dht;

import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author marcin
 */
public class DelDHTMessage extends CommMessage {
  private final KeyDHT key_;

  public DelDHTMessage(KeyDHT key) {
    super(null, null);
    key_ = key;
  }
}
