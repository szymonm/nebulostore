package org.nebulostore.communication.dht;

/**
 * @author marcin
 */
public class MergeableInteger implements IMergeable {

  private static final long serialVersionUID = -3772950029401952797L;
  private final int value_;

  public MergeableInteger(int value) {
    value_ = value;
  }

  @Override
  public IMergeable merge(IMergeable other) {
    return this;
  }

  public int getValue() {
    return value_;
  }

}
