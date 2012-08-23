package org.nebulostore.communication.kademlia;

import org.nebulostore.communication.dht.Mergeable;

public class MergeableLong implements Mergeable {

  private static final long serialVersionUID = -480496520354124642L;

  private final long value_;

  public MergeableLong(long value) {
    value_ = value;
  }

  public long getValue() {
    return value_;
  }

  @Override
  public Mergeable merge(Mergeable other) {
    return this;
  }

  @Override
  public String toString() {
    return "" + value_;
  }
}