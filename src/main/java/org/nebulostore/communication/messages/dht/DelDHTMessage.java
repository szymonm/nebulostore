package org.nebulostore.communication.messages.dht;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.dht.KeyDHT;

/**
 * @author marcin
 */
public class DelDHTMessage extends InDHTMessage {
  /**
   */
  private final KeyDHT key_;

  public DelDHTMessage(String jobId, KeyDHT key) {
    super(jobId);
    key_ = key;
  }

  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
