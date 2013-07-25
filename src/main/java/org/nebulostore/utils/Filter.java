package org.nebulostore.utils;

/**
 * Returns true for items that shouldn't be filtered out.
 * @author szymonmatejczyk
 *
 * @param <T> objects to be filtered
 */
public interface Filter<T> {
  boolean filter(T t);
}
