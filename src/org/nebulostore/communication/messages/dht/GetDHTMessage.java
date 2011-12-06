package org.nebulostore.communication.messages.dht;

import org.nebulostore.communication.dht.KeyDHT;

/**
 * @author marcin
 */
public class GetDHTMessage extends InDHTMessage {

  private final KeyDHT key_;

  public GetDHTMessage(KeyDHT key) {
    key_ = key;
  }

  public KeyDHT getKey() {
    return key_;
  }
}
