package pl.edu.uw.mimuw.nebulostore.appcore;

import java.util.LinkedList;

import pl.edu.uw.mimuw.nebulostore.replicator.DirectoryEntry;

/**
 * Temporary directory implemetation.
 */
public class Directory extends DataFile {
  private LinkedList<DirectoryEntry> entries_;

  public LinkedList<DirectoryEntry> getEntries() {
    return entries_;
  }

  public Directory(LinkedList<DirectoryEntry> entries) {
    super();
    entries_ = entries;
  }

  public Directory() {
    super();
    entries_ = new LinkedList<DirectoryEntry>();
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
    Directory other = (Directory) obj;
    if (entries_ == null) {
      if (other.entries_ != null)
        return false;
    } else if (!entries_.equals(other.entries_))
      return false;
    return true;
  }

}
