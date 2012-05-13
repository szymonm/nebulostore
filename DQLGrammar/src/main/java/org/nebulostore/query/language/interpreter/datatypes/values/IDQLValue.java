package org.nebulostore.query.language.interpreter.datatypes.values;

import java.io.Serializable;

import org.nebulostore.query.language.interpreter.datatypes.DQLType;
import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;
import org.nebulostore.query.privacy.PrivacyLevel;

public interface IDQLValue extends Serializable {

  PrivacyLevel getPrivacyLevel();

  IDQLValue addNum(IDQLValue arg) throws InterpreterException;

  IDQLValue multNum(IDQLValue arg) throws InterpreterException;

  IDQLValue divNum(IDQLValue arg) throws InterpreterException;

  IDQLValue subNum(IDQLValue arg) throws InterpreterException;

  IDQLValue numNegation() throws InterpreterException;

  IDQLValue modNum(IDQLValue arg) throws InterpreterException;

  IDQLValue equals(IDQLValue arg) throws InterpreterException;

  IDQLValue notEquals(IDQLValue arg) throws InterpreterException;

  IDQLValue less(IDQLValue arg) throws InterpreterException;

  IDQLValue lessEquals(IDQLValue arg) throws InterpreterException;

  IDQLValue greater(IDQLValue arg) throws InterpreterException;

  IDQLValue greaterEquals(IDQLValue arg) throws InterpreterException;

  IDQLValue and(IDQLValue arg) throws InterpreterException;

  IDQLValue or(IDQLValue arg) throws InterpreterException;

  IDQLValue not() throws InterpreterException;

  Object toJava();

  DQLType getType();

  void checkType(DQLType type) throws InterpreterException;

  void setToHigherOrEqualsPrivacyLevel(PrivacyLevel level) throws InterpreterException;

  boolean equal(Object o);
}
