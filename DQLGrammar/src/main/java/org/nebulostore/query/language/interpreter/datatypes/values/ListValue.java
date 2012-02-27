package org.nebulostore.query.language.interpreter.datatypes.values;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.nebulostore.query.language.interpreter.datatypes.DQLComplexType;
import org.nebulostore.query.language.interpreter.datatypes.DQLComplexType.DQLComplexTypeEnum;
import org.nebulostore.query.language.interpreter.datatypes.DQLType;
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
    List<DQLType> typeList = new LinkedList<DQLType>();
    typeList.add(type_);
    return new DQLComplexType(DQLComplexTypeEnum.DQLList, typeList);
  }

  public List<IDQLValue> getList() {
    return value_;
  }

  @Override
  public String toString() {
    String ret = "[ ListValue(";
    for (IDQLValue item : value_) {
      ret += item.toString() + ", ";
    }
    return ret + ") : " + getPrivacyLevel() + "]";
  }

  @Override
  public boolean add(IDQLValue e) {
    return value_.add(e);
  }

  @Override
  public void add(int index, IDQLValue element) {
    value_.add(index, element);
  }

  @Override
  public boolean addAll(Collection<? extends IDQLValue> c) {
    return value_.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends IDQLValue> c) {
    return value_.addAll(index, c);
  }

  @Override
  public void clear() {
    value_.clear();
  }

  @Override
  public boolean contains(Object o) {
    return value_.contains(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return value_.containsAll(c);
  }

  @Override
  public IDQLValue get(int index) {
    return value_.get(index);
  }

  @Override
  public int indexOf(Object o) {
    return value_.indexOf(o);
  }

  @Override
  public boolean isEmpty() {
    return value_.isEmpty();
  }

  @Override
  public int lastIndexOf(Object o) {
    return value_.lastIndexOf(o);
  }

  @Override
  public ListIterator<IDQLValue> listIterator() {
    return value_.listIterator();
  }

  @Override
  public ListIterator<IDQLValue> listIterator(int index) {
    return value_.listIterator(index);
  }

  @Override
  public boolean remove(Object o) {
    return value_.remove(o);
  }

  @Override
  public IDQLValue remove(int index) {
    return value_.remove(index);
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
    return value_.size();
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