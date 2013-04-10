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
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.model.EncryptedObject;

/**
 * @author bolek
 * Library of cryptographic and serialization functions.
 */
public final class CryptoUtils {
  private static Logger logger_ = Logger.getLogger(CryptoUtils.class);

  /**
   * Create cryptographically secure 128-bit long positive BigInteger.
   */
  public static BigInteger getRandomId() {
    BigInteger id =  new BigInteger(128, RANDOM);
    if (id.compareTo(new BigInteger("0")) == -1)
        id = id.negate().subtract(new BigInteger("-1"));
    return id;
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
      throw new CryptoException("IOError in serializing object.", exception);
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
      throw new CryptoException("IOError in deserializing object.", exception);
    } catch (ClassNotFoundException exception) {
      throw new CryptoException("Cannot deserialize object of unknown class.", exception);
    }
    return o;
  }

  public static String sha(EncryptedObject encryptedObject) {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      logger_.error(e.getMessage());
    }
    md.update(encryptedObject.getEncryptedData());
    return new String(md.digest(), Charset.forName("US-ASCII"));
  }

  private static final SecureRandom RANDOM = new SecureRandom();

  private CryptoUtils() { }
}
