package org.nebulostore.communication.gossip.core;

import java.io.Serializable;

import org.nebulostore.communication.address.CommAddress;

/**
 * @author Grzegorz Milka
 */
public class PeerDescriptor implements Serializable {
  private static final long serialVersionUID = -5313861167029739516L;
  private CommAddress peerAddress_;
  private int age_;

  public PeerDescriptor(CommAddress peerAddress) {
    peerAddress_ = peerAddress;
    age_ = 0;
  }

  public int getAge() {
    return age_;
  }

  public void setAge(int age) {
    age_ = age;
  }

  public CommAddress getPeerAddress() {
    return peerAddress_;
  }

  /**
   * Equal iff address is equal.
   */
  @Override
  public boolean equals(Object o) {
    return (o instanceof PeerDescriptor) &&
      (peerAddress_.equals(((PeerDescriptor) o).getPeerAddress()));
  }

  @Override
  public int hashCode() {
    return peerAddress_.hashCode();
  }

  @Override
  public String toString() {
    return "PeerDescriptor with address: " + getPeerAddress() +
      ", age: " + getAge();
  }
}
