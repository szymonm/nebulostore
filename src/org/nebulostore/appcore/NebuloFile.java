package org.nebulostore.appcore;

/**
 * @author bolek
 * File (raw data).
 */

public class NebuloFile extends NebuloObject {
  public byte[] data_;

  public NebuloFile() {
  }

  public NebuloFile(byte[] data) {
    data_ = data;
  }
}
