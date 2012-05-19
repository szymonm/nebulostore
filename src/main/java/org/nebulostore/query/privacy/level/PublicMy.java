package org.nebulostore.query.privacy.level;

import java.util.List;

import org.apache.log4j.Logger;
import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.PrivacyLevel;

public class PublicMy extends PrivacyLevel {

  private static Logger logger_ = Logger.getLogger(PublicMy.class);

  public PublicMy(List<DataSourcesSet> dataSources) {
    super(dataSources);
  }

  public PublicMy(DataSourcesSet dataSources) {
    super(dataSources);
  }

  public PublicMy() {
  }

  @Override
  public PrivacyLevel freshCopy() {
    return new PublicMy(dataSources_.freshCopy());
  }

  @Override
  protected PrivacyLevel performGeneralize(PrivacyLevel l, IDQLValue first, IDQLValue second, IDQLValue result)
      throws InterpreterException {

    logger_.debug("called perform generalize on privacy levels this: " + this +
        " other: " + l + " values: first: " + first + " second: " + second +
        " result: " + result);

    return this;
  }

  @Override
  protected PrivacyLevel performCompose(PrivacyLevel l, IDQLValue first, IDQLValue second, IDQLValue result)
      throws InterpreterException {
    return this;
  }

  @Override
  public boolean equals(Object o) {
    // TODO: Czy to jest poprawne
    return o instanceof PublicMy;
  }

  @Override
  public String toString() {
    return "PublicMy " + super.toString();
  }

  @Override
  public boolean canBeSent() {
    return true;
  }

}
