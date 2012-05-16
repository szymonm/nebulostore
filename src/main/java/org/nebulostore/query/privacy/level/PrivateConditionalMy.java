package org.nebulostore.query.privacy.level;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.PrivacyLevel;


public class PrivateConditionalMy extends PrivacyLevel {

  public PrivateConditionalMy(List<DataSourcesSet> dataSources) {
    super(dataSources);
  }

  public PrivateConditionalMy(DataSourcesSet dataSources) {
    super(dataSources);
  }

  public PrivateConditionalMy() {
  }

  @Override
  public PrivacyLevel freshCopy() {
    return new PrivateConditionalMy(dataSources_.freshCopy());
  }

  @Override
  protected PrivacyLevel performGeneralize(PrivacyLevel l, IDQLValue first, IDQLValue second, IDQLValue result)
      throws InterpreterException {
    if (((l.isMorePublicThan(this) && !l.equals(this)) || (l instanceof PrivateConditionalMy)) &&
        !l.getDataSources().hasNonEmptyIntersection(dataSources_) && changedValue(first, second, result)) {

      return new PublicConditionalMy(this.getDataSources().freshCopy().union(
          l.getDataSources()));
    }
    return this;
  }

  private boolean changedValue(IDQLValue first, IDQLValue second,
      IDQLValue result) {
    try {
      boolean ret = false;
      if (first != null && result != null)
        ret = ret || first.equal(result);
      if (second != null && result != null)
        ret = ret || second.equal(result);
      return ret;
    } catch (NotImplementedException ex) {
      return true;
    }

  }

  @Override
  protected PrivacyLevel performCompose(PrivacyLevel l, IDQLValue first, IDQLValue second, IDQLValue result)
      throws InterpreterException {
    return this;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof PrivateConditionalMy;
  }

  @Override
  public String toString() {
    return "PrivateConditionalMy " + super.toString();
  }

}
