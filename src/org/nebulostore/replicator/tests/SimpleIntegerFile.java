package org.nebulostore.replicator.tests;

import org.nebulostore.appcore.NebuloFile;

/**
 * @author szymonmatejczyk
 */
public class SimpleIntegerFile extends NebuloFile {
  /**
   */
  private static final long serialVersionUID = -7808434525397499375L;
  private Integer integer_;

  public SimpleIntegerFile(Integer integer) {
    super();
    this.integer_ = integer;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime + ((integer_ == null) ? 0 : integer_.hashCode());
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
    SimpleIntegerFile other = (SimpleIntegerFile) obj;
    if (integer_ == null) {
      if (other.integer_ != null)
        return false;
    } else if (!integer_.equals(other.integer_))
      return false;
    return true;
  }
}
