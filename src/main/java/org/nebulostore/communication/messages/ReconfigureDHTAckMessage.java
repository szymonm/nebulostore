package org.nebulostore.communication.messages;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * @author Marcin Walas
 */
public class ReconfigureDHTAckMessage extends Message {
  /**
   *
   */
  private static final long serialVersionUID = -7941538232588795699L;

  public ReconfigureDHTAckMessage(ReconfigureDHTMessage request) {
    super(request.getId());
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
