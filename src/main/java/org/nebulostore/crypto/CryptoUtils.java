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

import org.nebulostore.appcore.EncryptedObject;

/**
 * @author bolek
 * Library of cryptographic and serialization functions.
 */
public final class CryptoUtils {

  public static BigInteger getRandomId() {
    return new BigInteger(130, RANDOM);
  }

  public static String getRandomString() {
    return getRandomId().toString();
  }

  // TODO: Encryption must use cryptographic keys (add parameters?)
  public static EncryptedObject encryptObject(Serializable object) throws CryptoException {
    return new EncryptedObject(serializeObject(object));
  }

  public static Object decryptObject(EncryptedObject encryptedObject) throws
      CryptoException {
    return deserializeObject(encryptedObject.getEncryptedData());
  }

  public static byte[] encryptData(byte[] data) {
    return data;
  }

  public static byte[] decryptData(byte[] data) {
    return data;
  }

  public static byte[] serializeObject(Serializable object) throws CryptoException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] result;
    try {
      ObjectOutput out = new ObjectOutputStream(baos);
      out.writeObject(object);
      result = baos.toByteArray();
      out.close();
      baos.close();
    } catch (IOException exception) {
      throw new CryptoException("IOError in serializing object: " + exception.getMessage());
    }
    return result;
  }

  public static Object deserializeObject(byte[] serializedObject) throws CryptoException {
    Object o;
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(serializedObject);
      ObjectInput in = new ObjectInputStream(bais);
      o = in.readObject();
      bais.close();
      in.close();
    } catch (IOException exception) {
      throw new CryptoException("IOError in deserializing object: " + exception.getMessage());
    } catch (ClassNotFoundException exception) {
      throw new CryptoException("Cannot deserialize object of unknown class.");
    }
    return o;
  }

  private static final SecureRandom RANDOM = new SecureRandom();

  private CryptoUtils() { }
}
