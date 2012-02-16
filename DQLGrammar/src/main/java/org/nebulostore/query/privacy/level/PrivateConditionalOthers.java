package org.nebulostore.query.privacy.level;

import org.nebulostore.query.privacy.PrivacyLevel;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PrivateConditionalOthers extends PrivacyLevel {

  @Override
  public String toString() {
    return "PrivateConditionalOthers";
  }

  @Override
  public boolean isMorePublicThan(PrivacyLevel level) {
    throw new NotImplementedException();
  }

  // TODO: equals
}
