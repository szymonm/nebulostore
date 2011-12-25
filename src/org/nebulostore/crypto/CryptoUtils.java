package org.nebulostore.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.nebulostore.appcore.DirectoryEntry;
import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.NebuloObject;

/**
 * @author bolek
 * Library of cryptographic and serialization functions.
 */
public final class CryptoUtils {

  public static String getRandomName() {
    return new BigInteger(130, RANDOM).toString(32);
  }

  // TODO: Encryption must use cryptographic keys (add parameters?)
  public static EncryptedEntity encryptNebuloObject(NebuloObject object) throws IOException {
    return new EncryptedEntity(serializeObject(object));
  }

  public static NebuloObject decryptNebuloObject(EncryptedEntity encryptedObject) throws
      IOException, ClassNotFoundException {
    return (NebuloObject) deserializeObject(encryptedObject.getEncryptedData());
  }

  public static EncryptedEntity encryptDirectoryEntry(NebuloObject object) throws IOException {
    return new EncryptedEntity(serializeObject(object));
  }

  public static DirectoryEntry decryptDirectoryEntry(EncryptedEntity encryptedObject) throws
      IOException, ClassNotFoundException {
    return (DirectoryEntry) deserializeObject(encryptedObject.getEncryptedData());
  }

  public static byte[] serializeObject(Serializable object) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutput out = new ObjectOutputStream(baos);
    out.writeObject(object);
    byte[] result = baos.toByteArray();
    out.close();
    baos.close();
    return result;
  }

  public static Object deserializeObject(byte[] serializedObject) throws
      IOException, ClassNotFoundException {
    ByteArrayInputStream bais = new ByteArrayInputStream(serializedObject);
    ObjectInput in = new ObjectInputStream(bais);
    Object o = in.readObject();
    bais.close();
    in.close();
    return o;
  }

  private static final SecureRandom RANDOM = new SecureRandom();

  private CryptoUtils() { }
}
