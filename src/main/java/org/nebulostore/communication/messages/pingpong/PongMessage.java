package org.nebulostore.communication.messages.pingpong;

import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author Marcin Walas
 */
public class PongMessage extends CommMessage {
  private final int number_;

  public PongMessage(CommAddress destAddress, int number) {
    super(null, destAddress);
    number_ = number;
  }

  public int getNumber() {
    return number_;
  }
}
