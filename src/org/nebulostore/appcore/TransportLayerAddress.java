package org.nebulostore.appcore;

import java.io.Serializable;

/**
 * @author szymonmatejczyk.
 */
public class TransportLayerAddress implements Serializable {
  /**
   * Field for serialization purpose.
   */
  private static final long serialVersionUID = 1L;

  @Override
  public boolean equals(Object obj) {
    return this.getClass() == obj.getClass();
  }

  @Override
  public int hashCode() {
    return 0;
  }
}
