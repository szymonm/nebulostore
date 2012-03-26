package org.nebulostore.appcore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.appcore.exceptions.NebuloException;


/**
 * List of NebuloObjects.
 */
public class NebuloList extends NebuloObject implements Iterable<NebuloObject> {

  /**
   * List iterator.
   */
  public class ListIterator implements Iterator<NebuloObject> {
    private int currIndex_;

    public ListIterator() {
      currIndex_ = -1;
    }

    public NebuloObject next() {
      if (currIndex_ >= elements_.size() - 1) {
        throw new NoSuchElementException();
      } else {
        try {
          // TODO(bolek): Add some caching.
          return NebuloObject.fromAddress(elements_.get(++currIndex_));
        } catch (NebuloException exception) {
          // TODO(bolek): Not too descriptive...
          throw new NoSuchElementException(exception.getMessage());
        }
      }
    }

    @Override
    public boolean hasNext() {
      return currIndex_ < elements_.size() - 1;
    }

    @Override
    public void remove() {
      // TODO Auto-generated method stub
    }
  }

  private static final long serialVersionUID = 8346982029337955123L;

  private ArrayList<NebuloAddress> elements_;

  public NebuloList() {
    elements_ = new ArrayList<NebuloAddress>();
  }

  @Override
  public Iterator<NebuloObject> iterator() {
    return new ListIterator();
  }

  public void append(NebuloObject element) {
    elements_.add(element.getAddress());
    //TODO: commit immediately?
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = prime + ((elements_ == null) ? 0 : elements_.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NebuloList other = (NebuloList) obj;
    if (elements_ == null) {
      if (other.elements_ != null)
        return false;
    } else if (!elements_.equals(other.elements_))
      return false;
    return true;
  }

  @Override
  protected void runSync() {
  }
}
