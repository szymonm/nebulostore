package org.nebulostore.communication.messages;

import java.io.Serializable;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;

/**
 * Created as a base class for all messages that are being sent over communication layer.
 * @author  Marcin Walas
 */
public abstract class CommMessage extends Message implements Serializable {

  private CommAddress commSourceAddress_;
  private CommAddress commDestAddress_;

  public CommMessage(CommAddress sourceAddress, CommAddress destAddress) {
    commSourceAddress_ = sourceAddress;
    commDestAddress_ = destAddress;
  }

  public CommMessage(String jobId, CommAddress sourceAddress, CommAddress destAddress) {
    super(jobId);
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

  public void setDestinationAddress(CommAddress destAddress) {
    commDestAddress_ = destAddress;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
