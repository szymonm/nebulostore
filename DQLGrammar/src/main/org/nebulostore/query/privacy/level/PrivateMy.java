package org.nebulostore.query.privacy.level;

import org.nebulostore.query.privacy.PrivacyLevel;

public class PrivateMy extends PrivacyLevel {

  private static PrivateMy instance_;

  public static PrivacyLevel getInstance() {
    if (instance_ == null) {
      instance_ = new PrivateMy();
    }
    return instance_;
  }

  private PrivateMy() {

  }

  @Override
  public PrivacyLevel generalize(PrivacyLevel l) {
    return this;
  }

}
