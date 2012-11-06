package org.nebulostore.communication.messages.bdbdht;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.dht.DHTMessage;

/**
 * @author  Marcin Walas
 */
public class BdbMessageWrapper extends CommMessage {

  private static final long serialVersionUID = -7967599945241770910L;

  private final DHTMessage wrapped_;

  public BdbMessageWrapper(CommAddress sourceAddress, CommAddress destAddress,
      DHTMessage wrapped) {
    super(sourceAddress, destAddress);
    wrapped_ = wrapped;
  }

  public DHTMessage getWrapped() {
    return wrapped_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
