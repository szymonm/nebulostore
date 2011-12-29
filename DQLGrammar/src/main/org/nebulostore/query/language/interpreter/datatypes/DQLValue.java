package org.nebulostore.query.language.interpreter.datatypes;

import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class DQLValue implements IDQLValue {

  public enum DQLType {
    DQLInteger, DQLDouble, DQLString // TODO: all types
  };

  @Override
  public IDQLValue add(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue mult(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue div(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue sub(IDQLValue arg) throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue numNegation() throws InterpreterException {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue mod(IDQLValue arg) throws InterpreterException {
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
