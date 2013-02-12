package org.nebulostore.communication.dht;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

import org.bouncycastle.util.encoders.Base64;

/**
 * @author marcin
 */

public class ValueDHT implements Serializable {
  private static final long serialVersionUID = 8747517245988476609L;
  private final Mergeable value_;

  public Mergeable getValue() {
    return value_;
  }

  public ValueDHT(Mergeable v) {
    value_ = v;
  }

  public String serializeValue() {
    // TODO: Move it to utils.
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(baos);
      oos.writeObject(value_);
    } catch (IOException e) {
      e.printStackTrace();
    }

    String serialized = new String(Base64.encode(baos.toByteArray()),
            Charset.forName("UTF-8"));
    return serialized;
  }

  public static ValueDHT build(String serialized) {

    byte[] data = null;
    data = Base64.decode(serialized);
    ByteArrayInputStream baos = new ByteArrayInputStream(data);
    ObjectInputStream ois = null;
    try {
      ois = new ObjectInputStream(baos);
      return new ValueDHT((Mergeable) ois.readObject());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public String toString() {
    return value_.toString();
  }
}
