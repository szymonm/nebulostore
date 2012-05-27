package org.nebulostore.appcore;

/**
 * Represents file chunk.
 */
public class FileChunk extends NebuloObject {

  private static final long serialVersionUID = -8992804393601229112L;

  // Plain, unencrypted data.
  protected byte[] data_;

  public FileChunk() {
    data_ = new byte[0];
  }

  public FileChunk(int len) {
    data_ = new byte[len];
  }

  public byte[] getData() {
    return data_;
  }

  public void setData(byte[] data) {
    data_ = data;
  }

  @Override
  protected void runSync() {
    // TODO Auto-generated method stub
  }
}
