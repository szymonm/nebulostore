package org.nebulostore.replicaresolver;

import java.io.Serializable;

import org.nebulostore.communication.dht.core.ValueDHT;
import org.nebulostore.newcommunication.netutils.remotemap.Transaction;

/**
 * @author Grzegorz Milka
 */
public class MergeTransaction implements Transaction {
  private static final long serialVersionUID = 1L;
  private final ValueDHT valueDHT_;

  public MergeTransaction(ValueDHT valueDHT) {
    valueDHT_ = valueDHT;
  }

  @Override
  public Serializable performTransaction(int type, Serializable key, Serializable value) {
    ValueDHT valueDHT = valueDHT_;

    if (value != null) {
      ValueDHT oldValue = (ValueDHT) value;
      valueDHT = new ValueDHT(valueDHT_.getValue().merge(oldValue.getValue()));
    }

    return valueDHT;
  }

}
