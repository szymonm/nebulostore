package org.nebulostore.query.privacy.level;

import org.antlr.runtime.tree.CommonTree;
import org.nebulostore.query.privacy.PrivacyLevel;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PrivateConditionalMy extends PrivacyLevel {

  public PrivateConditionalMy(CommonTree expression) {
    // TODO Auto-generated constructor stub

  }

  @Override
  public String toString() {
    return "PrivateConditionalMy";
  }

  @Override
  public boolean isMorePublicThan(PrivacyLevel level) {
    throw new NotImplementedException();
  }

  // TODO: equals
}
