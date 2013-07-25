package org.nebulostore.utils;

import java.io.Serializable;

/**
 * @author szymonmatejczyk
 * @param <A>
 *          first element
 * @param <B>
 *          second element
 */
public class Pair<A, B> implements Serializable {
  private static final long serialVersionUID = 5172737595679197273L;

  private final A first_;
  private final B second_;

  public Pair(A a, B b) {
    first_ = a;
    second_ = b;
  }

  public A getFirst() {
    return first_;
  }

  public B getSecond() {
    return second_;
  }

}
