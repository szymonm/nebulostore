package pl.edu.uw.mimuw.nebulostore.communication.messages;

import java.io.Serializable;

import pl.edu.uw.mimuw.nebulostore.appcore.Message;
import pl.edu.uw.mimuw.nebulostore.communication.address.CommAddress;

/**
 * Created as a base class for all messages that are being sent over
 * communication layer.
 * 
 * @author Marcin Walas
 */
public abstract class CommMessage extends Message implements Serializable {
  private CommAddress commSourceAddress_;
  private final CommAddress commDestAddress_;

  public CommMessage(CommAddress sourceAddress, CommAddress destAddress) {
    commSourceAddress_ = sourceAddress;
    commDestAddress_ = destAddress;
  }

  public CommAddress getDestinationAddress() {
    return commDestAddress_;
  }

  public CommAddress getSourceAddress() {
    return commSourceAddress_;
  }

  public void setSourceAddress(CommAddress sourceAddress) {
    commSourceAddress_ = sourceAddress;
  }

}
