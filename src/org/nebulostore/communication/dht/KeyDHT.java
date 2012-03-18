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

  private static Logger logger_ = Logger.getLogger(KeyDHT.class);
  private final BigInteger key_;

  public KeyDHT(BigInteger key) {
    key_ = key;
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
