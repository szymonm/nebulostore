package org.nebulostore.query.privacy;

import java.io.Serializable;

import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

abstract public class PrivacyLevel implements Serializable {

  private static final long serialVersionUID = 5158282600253881775L;

  public boolean isMorePublicThan(PrivacyLevel level) {
    return PrivacyController.getInstance().morePublic(level, this);
  }

  public boolean isLessPublicThan(PrivacyLevel level) {
    return PrivacyController.getInstance().lessPublic(level, this);
  }

  public PrivacyLevel generalize(PrivacyLevel l) throws InterpreterException {
    if (isMorePublicThan(l))
      return l;
    if (isLessPublicThan(l))
      return this;
    throw new InterpreterException("Unable to generalize " + this + " with " +
        l);
  }

  @Override
  public abstract String toString();
}
