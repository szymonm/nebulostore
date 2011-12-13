package org.nebulostore.communication.messages.dht;

/**
 * @author marcin
 */
public abstract class OutDHTMessage extends DHTMessage {
  public OutDHTMessage(InDHTMessage reqMessage) {
    super(reqMessage.getId());
  }
}
