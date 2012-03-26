package org.nebulostore.communication.messages.bdbdht;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.dht.DHTMessage;

/**
 * @author  Marcin Walas
 */
public class BdbMessageWrapper extends CommMessage {

  /**
   */
  private final DHTMessage wrapped_;

  public BdbMessageWrapper(CommAddress sourceAddress, CommAddress destAddress,
      DHTMessage wrapped) {
    super(sourceAddress, destAddress);
    wrapped_ = wrapped;
  }

  public DHTMessage getWrapped() {
    return wrapped_;
  }

}
