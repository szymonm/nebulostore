package org.nebulostore.appcore;

/**
 * @author bolek
 * File (raw data).
 */

public class NebuloFile extends NebuloObject {
  private static final long serialVersionUID = -1687075358113579488L;

  public byte[] data_;

  public NebuloFile() { }

  public NebuloFile(byte[] data) {
    data_ = data;
  }
}
