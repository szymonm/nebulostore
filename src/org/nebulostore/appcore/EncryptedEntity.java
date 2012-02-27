package org.nebulostore.appcore;

import java.io.Serializable;
/**
 * @author bolek
 * Wrapper for encrypted data.
 */
public class EncryptedEntity implements Serializable {
  private static final long serialVersionUID = -4442741001160904578L;

  public EncryptedEntity(byte[] encryptedData) {
    encryptedData_ = encryptedData;
  }

  public byte[] getEncryptedData() {
    return encryptedData_;
  }

  private byte[] encryptedData_;
}
