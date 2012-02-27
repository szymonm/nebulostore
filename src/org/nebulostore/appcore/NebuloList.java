package org.nebulostore.appcore;

import java.util.ArrayList;

import org.nebulostore.addressing.NebuloAddress;


/**
 * List of NebuloObjects.
 */
public class NebuloList extends NebuloObject {
  private static final long serialVersionUID = 8346982029337955123L;

  private ArrayList<NebuloAddress> elements_;

  public NebuloList() {
    elements_ = new ArrayList<NebuloAddress>();
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
