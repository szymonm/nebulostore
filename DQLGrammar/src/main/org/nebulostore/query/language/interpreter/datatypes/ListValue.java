package org.nebulostore.query.language.interpreter.datatypes;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.nebulostore.query.privacy.PrivacyLevel;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ListValue extends DQLValue implements List<IDQLValue> {

  private final List<IDQLValue> value_;
  private int size_;
  private final DQLType type_;

  public ListValue(DQLType type, PrivacyLevel privacyLevel) {
    super(privacyLevel);
    value_ = new LinkedList<IDQLValue>();
    type_ = type;
  }

  public ListValue(List<IDQLValue> value, DQLType type,
      PrivacyLevel privacyLevel) {
    super(privacyLevel);
    value_ = new LinkedList<IDQLValue>(value);
    type_ = type;
  }

  @Override
  public Object toJava() {
    return JavaValuesGlue.fromDQL(value_);
  }

  @Override
  public Iterator<IDQLValue> iterator() {
    return value_.iterator();
  }

  @Override
  public DQLType getType() {
    return DQLType.DQLList;
  }

  public List<IDQLValue> getList() {
    return value_;
  }

  @Override
  public boolean add(IDQLValue e) {
    throw new NotImplementedException();
  }

  @Override
  public void add(int index, IDQLValue element) {
    throw new NotImplementedException();
  }

  @Override
  public boolean addAll(Collection<? extends IDQLValue> c) {
    throw new NotImplementedException();
  }

  @Override
  public boolean addAll(int index, Collection<? extends IDQLValue> c) {
    throw new NotImplementedException();
  }

  @Override
  public void clear() {
    throw new NotImplementedException();
  }

  @Override
  public boolean contains(Object o) {
    throw new NotImplementedException();
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue get(int index) {
    throw new NotImplementedException();
  }

  @Override
  public int indexOf(Object o) {
    throw new NotImplementedException();
  }

  @Override
  public boolean isEmpty() {
    throw new NotImplementedException();
  }

  @Override
  public int lastIndexOf(Object o) {
    throw new NotImplementedException();
  }

  @Override
  public ListIterator<IDQLValue> listIterator() {
    throw new NotImplementedException();
  }

  @Override
  public ListIterator<IDQLValue> listIterator(int index) {
    throw new NotImplementedException();
  }

  @Override
  public boolean remove(Object o) {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue remove(int index) {
    throw new NotImplementedException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new NotImplementedException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new NotImplementedException();
  }

  @Override
  public IDQLValue set(int index, IDQLValue element) {
    throw new NotImplementedException();
  }

  @Override
  public int size() {
    throw new NotImplementedException();
  }

  @Override
  public List<IDQLValue> subList(int fromIndex, int toIndex) {
    throw new NotImplementedException();
  }

  @Override
  public Object[] toArray() {
    throw new NotImplementedException();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    throw new NotImplementedException();
  }

}
