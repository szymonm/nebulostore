package org.nebulostore.addressing;

import java.io.Serializable;

/**
 * Application Key.
 */
public class AppKey implements Serializable {
  private static final long serialVersionUID = -5977296486784377545L;
  private String key_;

  public AppKey(String key) {
    key_ = key;
  }

  public String getKey() {
    return key_;
  }

  public void setKey(String key) {
    key_ = key;
  }
}
