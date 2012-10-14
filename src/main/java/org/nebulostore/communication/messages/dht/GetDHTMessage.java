package org.nebulostore.communication.messages.dht;

import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.dht.KeyDHT;

/**
 * @author marcin
 */
public class GetDHTMessage extends InDHTMessage {

  /**
   */
  private final KeyDHT key_;

  public GetDHTMessage(String jobId, KeyDHT key) {
    super(jobId);
    key_ = key;
  }

  public KeyDHT getKey() {
    return key_;
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "GetDHTMessage for key: " + getKey();
  }

}
