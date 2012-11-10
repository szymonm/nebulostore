package org.nebulostore.query.language.interpreter.datatypes.values;

import org.nebulostore.query.privacy.PrivacyLevel;

public abstract class NumericValue extends DQLValue {

  public NumericValue(PrivacyLevel privacyLevel) {
    super(privacyLevel);
  }

  public abstract boolean isNeutral();
}
