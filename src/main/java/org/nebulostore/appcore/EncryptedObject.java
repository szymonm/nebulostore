package org.nebulostore.appcore;

import java.io.Serializable;
/**
 * Wrapper for encrypted data. EncryptedObjects are stored in Replicator.
 * @author bolek
 */
public class EncryptedObject implements Serializable {
  private static final long serialVersionUID = -4442741001160904578L;

  private byte[] encryptedData_;

  public EncryptedObject(byte[] encryptedData) {
    encryptedData_ = encryptedData;
  }

  public byte[] getEncryptedData() {
    return encryptedData_;
  }
}
