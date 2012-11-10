package org.nebulostore.query.privacy.level;

import java.util.List;

import org.nebulostore.query.language.interpreter.datasources.ConstantDataSource;
import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.datatypes.values.NumericValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.PrivacyLevel;

public class PublicConditionalMy extends PrivacyLevel {

  public PublicConditionalMy() {

  }

  public PublicConditionalMy(List<DataSourcesSet> dataSources) {
    super(dataSources);
  }

  public PublicConditionalMy(DataSourcesSet dataSources) {
    super(dataSources);
  }

  @Override
  public PrivacyLevel freshCopy() {
    return new PublicConditionalMy(dataSources_.freshCopy());
  }

  @Override
  protected PrivacyLevel performGeneralize(PrivacyLevel l, IDQLValue first,
      IDQLValue second, IDQLValue result, boolean isNeutralGenerator)
          throws InterpreterException {

    PrivacyLevel toRet = this;

    if (l.getDataSources().hasNonEmptyIntersection(dataSources_))
      toRet = new PrivateConditionalMy(dataSources_.freshCopy().union(
          l.getDataSources()));

    if (isNeutralGenerator && (result instanceof NumericValue) &&
        ((NumericValue) result).isNeutral() &&
        getDataSources().hasNonEmptyIntersection(l.getDataSources())) {
      toRet.getDataSources().add(ConstantDataSource.getInstance());
    }
    return toRet;
  }

  @Override
  protected PrivacyLevel performCompose(PrivacyLevel l, IDQLValue first,
      IDQLValue second, IDQLValue result) throws InterpreterException {
    // TODO: Test it!
    if (l.getDataSources().hasNonEmptyIntersection(dataSources_))
      return new PrivateConditionalMy(dataSources_.freshCopy().union(
          l.getDataSources()));
    return this;
  }

  @Override
  public boolean equals(Object o) {
    // TODO: Czy to jest poprawne
    return o instanceof PublicConditionalMy;
  }

  @Override
  public String toString() {
    return "PublicConditionalMy " + super.toString();
  }

  @Override
  public boolean canBeSent() {
    return true;
  }

}
