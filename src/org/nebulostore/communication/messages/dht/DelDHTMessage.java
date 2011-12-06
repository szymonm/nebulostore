package org.nebulostore.communication.messages.dht;

import org.nebulostore.communication.dht.KeyDHT;

/**
 * @author marcin
 */
public class DelDHTMessage extends InDHTMessage {
  private final KeyDHT key_;

  public DelDHTMessage(KeyDHT key) {
    key_ = key;
  }
}
