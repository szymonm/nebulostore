package org.nebulostore.replicator;

import java.util.ArrayList;

import org.nebulostore.appcore.DataFile;

/**
 * @author szymonmatejczyk
 */

public class DirectoryPhysicalObject extends DataFile {

  /**
   */
  private static final long serialVersionUID = -5692850350761671333L;

  private ArrayList<DirectoryEntry> entries_ = new ArrayList<DirectoryEntry>();

  public void addEntry(DirectoryEntry entry) {
    entries_.add(entry);
  }

  public ArrayList<DirectoryEntry> getEntries() {
    return entries_;
  }

}
