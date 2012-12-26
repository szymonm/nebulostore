package org.nebulostore.communication.dht;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * @author marcin
 */
public class KeyDHT implements Serializable {
  private static final long serialVersionUID = 3261126809266422452L;
  private static Logger logger_ = Logger.getLogger(KeyDHT.class);
  private final BigInteger key_;

  public KeyDHT(BigInteger key) {
    key_ = key;
  }

  public KeyDHT(String key) {
    key_ = new BigInteger(key);
  }

  @Override
  public String toString() {
    return key_.toString();
  }

  public byte[] getBytes() {
    return key_.toByteArray();
  }

  public BigInteger getBigInt() {
    return key_;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 37 * result + ((key_ == null) ? 0 : key_.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    KeyDHT other = (KeyDHT) obj;
    if (key_ == null) {
      if (other.key_ != null)
        return false;
    } else if (!key_.equals(other.key_))
      return false;
    return true;
  }

  public static KeyDHT fromSerializableObject(Serializable object)  {
    // TODO: This - getBytes() is platform dependent, change this

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(baos);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    try {
      oos.writeObject(object);
    } catch (IOException e) {
      e.printStackTrace();
    }

    byte[] val = baos.toByteArray();

    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-1");
      return new KeyDHT(new BigInteger(1, md.digest(val)));
    } catch (NoSuchAlgorithmException e) {
      logger_.fatal(e);
      System.exit(-1);
      return null;
    }
  }
}
