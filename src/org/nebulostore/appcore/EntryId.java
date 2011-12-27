package org.nebulostore.appcore;

import java.io.Serializable;

/**
 * @author bolek
 * Entry ID. Needs to be 'Comparable' as it is used as Map key.
 */
public class EntryId implements Serializable, Comparable<EntryId> {
  public String entryId_;

  public EntryId(String entryId) {
    entryId_ = entryId;
  }

  @Override
  public int compareTo(EntryId arg0) {
    return entryId_.compareTo(arg0.entryId_);
  }
}
