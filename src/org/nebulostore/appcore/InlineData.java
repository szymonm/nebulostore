package org.nebulostore.appcore;

/**
 * @author bolek
 * Small chunk of data stored in a directory entry to avoid excessive replication.
 */
public class InlineData extends DirectoryEntry {
  public byte[] data_;

  public NebuloFile toNebuloFile() {
    return new NebuloFile(data_);
  }
}
