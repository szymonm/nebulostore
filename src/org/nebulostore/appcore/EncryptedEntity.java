package org.nebulostore.appcore;

import java.io.Serializable;
/**
 * @author bolek
 * Wrapper for encrypted data.
 */
public class EncryptedEntity implements Serializable {
  public EncryptedEntity(byte[] encryptedData) {
    encryptedData_ = encryptedData;
  }

  public byte[] getEncryptedData() {
    return encryptedData_;
  }

  private byte[] encryptedData_;
}
