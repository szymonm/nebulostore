package org.nebulostore.communication.kademlia;

import org.nebulostore.communication.dht.Mergeable;

public class MergeableString implements Mergeable {

  private static final long serialVersionUID = -2869778283851635173L;

  private final String value_;

  public MergeableString(String value) {
    value_ = value;
  }

  public String getValue() { return value_;}

  @Override
  public Mergeable merge(Mergeable other) {
    return this;
  }

  @Override
  public String toString() {
    return value_;
  }

}
