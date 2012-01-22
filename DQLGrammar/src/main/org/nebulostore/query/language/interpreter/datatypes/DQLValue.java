package org.nebulostore.query.language.interpreter.datatypes;

import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.PrivacyLevel;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class DQLValue implements IDQLValue {

  protected final PrivacyLevel privacyLevel_;

  @Override
  public PrivacyLevel getPrivacyLevel() {
    return privacyLevel_;
  }

  public DQLValue(PrivacyLevel privacyLevel) {
    privacyLevel_ = privacyLevel;
  }

  public enum DQLType {
    DQLInteger, DQLDouble, DQLString, DQLLambda, DQLList, DQLTuple, DQLBoolean
  };

  @Override
  public IDQLValue addNum(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue multNum(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue divNum(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue subNum(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue numNegation() throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue modNum(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue equals(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue notEquals(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue less(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue lessEquals(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue greater(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue greaterEquals(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue and(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue or(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue not() throws InterpreterException {
    throw new NotImplementedException();
  }

}
