package org.nebulostore.utils;

public class Pair<A, B> {

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
