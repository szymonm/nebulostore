package pl.edu.uw.mimuw.nebulostore.communication.messages.pingpong;

import pl.edu.uw.mimuw.nebulostore.communication.address.CommAddress;
import pl.edu.uw.mimuw.nebulostore.communication.messages.CommMessage;

/**
 * @author Marcin Walas
 */
public class PingMessage extends CommMessage {
  private final int number_;

  public PingMessage(CommAddress destAddress, int number) {
    super(null, destAddress);

    number_ = number;
  }

  public int getNumber() {
    return number_;
  }
}
