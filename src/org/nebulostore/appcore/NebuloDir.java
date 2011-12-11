package org.nebulostore.appcore;

import java.util.Map;
import java.util.TreeMap;


/**
 * Temporary directory implementation.
 */
public class NebuloDir extends NebuloObject {
  private Map<EntryId, EncryptedEntity> entries_;

  public Map<EntryId, EncryptedEntity> getEntries() {
    return entries_;
  }

  public NebuloDir(Map<EntryId, EncryptedEntity> entries) {
    super();
    entries_ = entries;
  }

  public NebuloDir() {
    super();
    entries_ = new TreeMap<EntryId, EncryptedEntity>();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = prime + ((entries_ == null) ? 0 : entries_.hashCode());
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
    NebuloDir other = (NebuloDir) obj;
    if (entries_ == null) {
      if (other.entries_ != null)
        return false;
    } else if (!entries_.equals(other.entries_))
      return false;
    return true;
  }

}
