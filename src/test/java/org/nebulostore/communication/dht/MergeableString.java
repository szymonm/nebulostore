package org.nebulostore.communication.dht;

/**
 * @author bolek
 */
public class MergeableString implements IMergeable {

  private static final long serialVersionUID = -3772950029401952797L;
  private final String value_;

  public MergeableString(String value) {
    value_ = value;
  }

  @Override
  public IMergeable merge(IMergeable other) {
    return this;
  }

  public String getValue() {
    return value_;
  }

}
