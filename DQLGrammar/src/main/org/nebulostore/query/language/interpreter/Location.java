package org.nebulostore.query.language.interpreter;

public class Location implements Comparable<Object> {

  static long globalLocationId = 0;
  long locationId = globalLocationId++;

  @Override
  public int compareTo(Object arg) {
    if (arg instanceof Location) {
      return ((Location) arg).locationId == locationId ? 0
          : (((Location) arg).locationId > locationId ? 1 : -1);
    } else {
      return 1;
    }

  }
}
