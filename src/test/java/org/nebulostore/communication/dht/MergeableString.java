package org.nebulostore.communication.dht;

/**
 * @author bolek
 */
public class MergeableString implements Mergeable {

  private static final long serialVersionUID = -3772950029401952797L;
  private final String value_;

  public MergeableString(String value) {
    value_ = value;
  }

  @Override
  public Mergeable merge(Mergeable other) {
    return this;
  }

  public String getValue() {
    return value_;
  }

}
