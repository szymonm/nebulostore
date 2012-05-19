package org.nebulostore.query.privacy;

import java.io.Serializable;
import java.util.List;

import org.nebulostore.query.language.interpreter.datasources.DataSourcesSet;
import org.nebulostore.query.language.interpreter.datatypes.values.IDQLValue;
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

  /**
   * Perform generalization of the privacy type
   * 
   * @param l
   * @return
   * @throws InterpreterException
   */
  public PrivacyLevel generalize(PrivacyLevel l, IDQLValue first,
      IDQLValue second, IDQLValue result) throws InterpreterException {
    if (isMorePublicThan(l))
      return l.freshCopy().performGeneralize(this, first, second, result)
          .mergeSources(this);
    if (isLessPublicThan(l))
      return this.freshCopy().performGeneralize(l, first, second, result)
          .mergeSources(l);
    throw new InterpreterException("Unable to generalize " + this + " with " +
        l);
  }

  protected abstract PrivacyLevel performGeneralize(PrivacyLevel l,
      IDQLValue first, IDQLValue second, IDQLValue result)
          throws InterpreterException;

  /**
   * Perform composition of privacy type
   * 
   * @param privacyLevel
   * @return
   */
  public PrivacyLevel compose(PrivacyLevel l,IDQLValue first,
      IDQLValue second, IDQLValue result) throws InterpreterException {
    if (isMorePublicThan(l))
      return l.freshCopy().performCompose(this, first, second, result)
          .mergeSources(this);
    if (isLessPublicThan(l))
      return this.freshCopy().performCompose(l, first, second, result)
          .mergeSources(l);
    throw new InterpreterException("Unable to compose " + this + " with " + l);
  }

  protected abstract PrivacyLevel performCompose(PrivacyLevel l,
      IDQLValue first, IDQLValue second, IDQLValue result)
          throws InterpreterException;

  public abstract boolean canBeSent();

  public PrivacyLevel mergeSources(PrivacyLevel l) {
    dataSources_ = dataSources_.freshCopy().union(l.dataSources_);
    return this;
  }

  public abstract PrivacyLevel freshCopy();

  @Override
  public String toString() {
    return "[" + dataSources_.toString() + "]";
  }

  @Override
  public abstract boolean equals(Object o);

  public PrivacyLevel purgeSources() {
    dataSources_ = new DataSourcesSet();
    return this;
  }

}
