package org.nebulostore.communication.dht;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bouncycastle.util.encoders.Base64;

/**
 * @author marcin
 */

public class ValueDHT implements Serializable {

  private final IMergeable value_;

  public IMergeable getValue() {
    return value_;
  }

  public ValueDHT(IMergeable v) {
    value_ = v;
  }

  public String serializeValue() {
    String serialized = new String();
    // TODO: Move it to utils.
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(baos);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    try {
      oos.writeObject(value_);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    serialized = new String(Base64.encode(baos.toByteArray()));
    return serialized;
  }

  public static ValueDHT build(String serialized) {

    byte[] data = null;
    //    try {
    data = Base64.decode(serialized);
    //    } catch (IOException e2) {
    // TODO Auto-generated catch block
    //      e2.printStackTrace();
    //    }
    ByteArrayInputStream baos = new ByteArrayInputStream(data);
    ObjectInputStream ois = null;
    try {
      ois = new ObjectInputStream(baos);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    try {
      return new ValueDHT((IMergeable) ois.readObject());
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public String toString() {
    return value_.toString();
  }
}
