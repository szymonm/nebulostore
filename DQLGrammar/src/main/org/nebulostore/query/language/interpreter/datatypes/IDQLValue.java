package org.nebulostore.query.language.interpreter.datatypes;

import org.nebulostore.query.language.interpreter.exceptions.InterpreterException;

public interface IDQLValue {

  IDQLValue add(IDQLValue arg) throws InterpreterException;

  IDQLValue mult(IDQLValue arg) throws InterpreterException;

  IDQLValue div(IDQLValue arg) throws InterpreterException;

  IDQLValue sub(IDQLValue arg) throws InterpreterException;

  IDQLValue numNegation() throws InterpreterException;

  IDQLValue mod(IDQLValue arg) throws InterpreterException;

  IDQLValue equals(IDQLValue arg) throws InterpreterException;

  IDQLValue notEquals(IDQLValue arg) throws InterpreterException;

  IDQLValue less(IDQLValue arg) throws InterpreterException;

  IDQLValue lessEquals(IDQLValue arg) throws InterpreterException;

  IDQLValue greater(IDQLValue arg) throws InterpreterException;

  IDQLValue greaterEquals(IDQLValue arg) throws InterpreterException;

  IDQLValue and(IDQLValue arg) throws InterpreterException;

  IDQLValue or(IDQLValue arg) throws InterpreterException;

  IDQLValue not() throws InterpreterException;
}
