package org.nebulostore.query.privacy;

import java.io.Serializable;
import java.util.List;

import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public abstract class PrivacyLevel implements Serializable {

  private static final long serialVersionUID = 5158282600253881775L;
  protected DataSourcesSet dataSources_;

  public PrivacyLevel(List<DataSourcesSet> dataSources) {
    DataSourcesSet initial = new DataSourcesSet();
    if (dataSources != null) {
      for (DataSourcesSet set : dataSources) {
        initial = initial.union(set);
      }
    }
    dataSources_ = initial;
  }

  public PrivacyLevel() {
    dataSources_ = new DataSourcesSet();
  }

  public PrivacyLevel(DataSourcesSet dataSources) {
    dataSources_ = dataSources;
  }

  public DataSourcesSet getDataSources() {
    return dataSources_;
  }

  public boolean isMorePublicThan(PrivacyLevel level) {
    return PrivacyController.getInstance().morePublic(level, this);
  }

  public boolean isLessPublicThan(PrivacyLevel level) {
    return PrivacyController.getInstance().lessPublic(level, this);
  }

  public PrivacyLevel generalize(PrivacyLevel l) throws InterpreterException {
    if (isMorePublicThan(l))
      return l.freshCopy().mergeSources(this);
    if (isLessPublicThan(l))
      return this.freshCopy().mergeSources(l);
    throw new InterpreterException("Unable to generalize " + this + " with " +
        l);
  }

  private PrivacyLevel mergeSources(PrivacyLevel l) {
    dataSources_ = dataSources_.union(l.dataSources_);
    return this;
  }

  protected abstract PrivacyLevel freshCopy();

  @Override
  public abstract String toString();

  @Override
  public abstract boolean equals(Object o);

}
