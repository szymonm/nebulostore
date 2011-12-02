package org.nebulostore.communication.messages.dht;

import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author marcin
 */
public class GetDHTMessage extends CommMessage {

  private final KeyDHT key_;

  public GetDHTMessage(KeyDHT key) {
    super(null, null);
    key_ = key;
  }
}
