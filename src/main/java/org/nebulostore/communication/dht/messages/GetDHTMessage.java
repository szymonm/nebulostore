package org.nebulostore.communication.dht.messages;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.communication.dht.core.KeyDHT;

/**
 * @author marcin
 */
public class GetDHTMessage extends InDHTMessage {
  private static final long serialVersionUID = -6134658511663501107L;
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
