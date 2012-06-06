package org.nebulostore.appcore;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.nebulostore.api.WriteNebuloObjectModule;
import org.nebulostore.appcore.exceptions.ListMergeException;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * List of NebuloObjects.
 */
public class NebuloList extends NebuloObject implements Iterable<NebuloElement> {
  private static final long serialVersionUID = 8346982029337955123L;

  protected ArrayList<NebuloElement> elements_;
  protected Set<BigInteger> removedIds_;

  /**
   * List iterator.
   */
  public class ListIterator implements Iterator<NebuloElement> {
    private int currIndex_;
    private final Iterator<NebuloElement> iterator_;

    public ListIterator() {
      currIndex_ = -1;
      iterator_ = elements_.iterator();
    }

    @Override
    public NebuloElement next() {
      NebuloElement ret = iterator_.next();
      // Only if next() does not throw exception.
      ++currIndex_;
      return ret;
    }

    @Override
    public boolean hasNext() {
      return currIndex_ < elements_.size() - 1;
    }

    @Override
    public void remove() {
      if (currIndex_ < 0) {
        throw new IllegalStateException();
      }
      BigInteger id = elements_.get(currIndex_).elementId_;
      iterator_.remove();
      removedIds_.add(id);
    }
  }

  public NebuloList() {
    elements_ = new ArrayList<NebuloElement>();
    removedIds_ = new TreeSet<BigInteger>();
  }

  @Override
  public Iterator<NebuloElement> iterator() {
    return new ListIterator();
  }

  public void append(NebuloElement element) throws NebuloException {
    elements_.add(element);
    //TODO(bolek): Should we commit immediately?
    runSync();
  }

  public void add(ListIterator iterator, NebuloElement element) throws NebuloException {
    int index = iterator.currIndex_ + 1;
    elements_.add(index, element);
    //TODO(bolek): Should we commit immediately?
    runSync();
  }

  /**
   * Tries to merge this list with other. Throws exception and does not change anything
   * when merging is not possible (e.g. there are ordering conflicts).
   * @param other  the other list to merge with
   */
  public void mergeWith(NebuloList other) throws ListMergeException {
    ArrayList<NebuloElement> newList = new ArrayList<NebuloElement>();
    TreeSet<BigInteger> allRemoved = new TreeSet<BigInteger>();
    allRemoved.addAll(removedIds_);
    allRemoved.addAll(other.removedIds_);

    TreeSet<BigInteger> myElements = new TreeSet<BigInteger>();
    TreeSet<BigInteger> myAdded = new TreeSet<BigInteger>();
    Iterator<NebuloElement> myIter = elements_.iterator();
    while (myIter.hasNext()) {
      myElements.add(myIter.next().elementId_);
    }

    int myIdx = 0;
    int otherIdx = 0;
    while (myIdx < elements_.size() && otherIdx < other.elements_.size()) {
      NebuloElement myElem = elements_.get(myIdx);
      NebuloElement otherElem = other.elements_.get(otherIdx);
      if (myElem.equals(otherElem)) {
        // Same element at the beginning of both lists.
        addIfNotRemoved(newList, myElem, allRemoved);
        myElements.remove(myElem.elementId_);
        myAdded.add(myElem.elementId_);
        ++myIdx;
        ++otherIdx;
      } else if (myElements.contains(otherElem.elementId_)) {
        // First list's head should come first.
        addIfNotRemoved(newList, myElem, allRemoved);
        myElements.remove(myElem.elementId_);
        myAdded.add(myElem.elementId_);
        ++myIdx;
      } else if (myAdded.contains(otherElem.elementId_)) {
        // Cycle in merged orders.
        throw new ListMergeException();
      } else {
        // Second list's head is a new element.
        addIfNotRemoved(newList, otherElem, allRemoved);
        ++otherIdx;
      }
    }
    while (myIdx < elements_.size()) {
      addIfNotRemoved(newList, elements_.get(myIdx++), allRemoved);
    }
    while (otherIdx < other.elements_.size()) {
      NebuloElement otherElem = other.elements_.get(otherIdx);
      if (myAdded.contains(otherElem.elementId_)) {
        // Cycle in merged orders.
        throw new ListMergeException();
      } else {
        addIfNotRemoved(newList, otherElem, allRemoved);
        ++otherIdx;
      }
    }
    elements_ = newList;
  }

  private void addIfNotRemoved(ArrayList<NebuloElement> list, NebuloElement elem,
      Set<BigInteger> removed) {
    if (!removed.contains(elem.elementId_)) {
      list.add(elem);
    }
  }

  @Override
  protected void runSync() throws NebuloException {
    WriteNebuloObjectModule writer = new WriteNebuloObjectModule(address_, this, dispatcherQueue_,
        previousVersions_);
    writer.getResult(TIMEOUT_SEC);
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
}
