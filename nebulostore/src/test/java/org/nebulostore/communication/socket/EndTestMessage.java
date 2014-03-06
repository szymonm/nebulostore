package org.nebulostore.communication.socket;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Message used by some unit tests to initiate shutdown of test.
 *
 * @author Grzegorz Milka
 *
 */
public class EndTestMessage extends CommMessage {
  private static final long serialVersionUID = 3573650064698119961L;

  public EndTestMessage(CommAddress sourceAddress, CommAddress destAddress) {
    super(sourceAddress, destAddress);
  }
}
