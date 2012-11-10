package org.nebulostore.replicator.messages;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

/**
 * Send as answer to StoreObjectMessage when object on this replica is not up to date.
 * @author szymonmatejczyk
 *
 */
public class UpdateWithholdMessage extends CommMessage {
  private static final long serialVersionUID = 2939806231367561120L;

  public UpdateWithholdMessage(String jobId, CommAddress sourceAddress,
      CommAddress destAddress, Reason reason) {
    super(jobId, sourceAddress, destAddress);
    reason_ = reason;
  }

  /**
   * Reason for withholding transaction.
   */
  public enum Reason { OBJECT_OUT_OF_DATE, SAVE_FAILURE, TIMEOUT }

  private final Reason reason_;

  public Reason getReason() {
    return reason_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
