package org.nebulostore.communication.kademlia;

import org.nebulostore.communication.dht.IMergeable;

public class MergeableString implements IMergeable {

  private static final long serialVersionUID = -2869778283851635173L;

  private final String value_;

  public MergeableString(String value) {
    value_ = value;
  }

  public String getValue() { return value_; }

  @Override
  public IMergeable merge(IMergeable other) {
    return this;
  }

  @Override
  public String toString() {
    return value_;
  }

}
