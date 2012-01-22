package org.nebulostore.query.privacy;

abstract public class PrivacyLevel {
  public abstract PrivacyLevel generalize(PrivacyLevel l);
}
