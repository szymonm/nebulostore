package org.nebulostore.replicator.messages;

import org.nebulostore.communication.address.CommAddress;

/**
 * Send as answer to StoreObjectMessage when object on this replica is not up to date.
 * @author szymonmatejczyk
 *
 */
public class UpdateWithholdMessage extends OutReplicatorMessage {
  private static final long serialVersionUID = 2939806231367561120L;

  public UpdateWithholdMessage(String jobId, CommAddress destAddress, Reason reason) {
    super(jobId, destAddress);
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
}
