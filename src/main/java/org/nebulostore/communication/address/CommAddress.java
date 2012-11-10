package org.nebulostore.communication.address;

import java.io.Serializable;
import java.util.UUID;

import org.nebulostore.communication.dht.KeyDHT;

/**
 * @author Grzegorz Milka
 */
public class CommAddress implements Serializable {
  private static final long serialVersionUID = -1730034659685291738L;
  private final UUID uuid_;
  private static CommAddress zeroCommAddress_;

  public CommAddress(long mostSigBits, long leastSigBits) {
    uuid_ = new UUID(mostSigBits, leastSigBits);
  }

  public CommAddress(UUID uuid) {
    uuid_ = uuid;
  }

  public static CommAddress newRandomCommAddress() {
    return new CommAddress(UUID.randomUUID());
  }

  /**
   * Return CommAddress symbolizing any or none address depending on context.
   */
  public static CommAddress getZero() {
    if (zeroCommAddress_ == null) {
      zeroCommAddress_ = new CommAddress(0L, 0L);
    }
    return zeroCommAddress_;
  }

  public UUID getPeerId() {
    return uuid_;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof CommAddress) &&
      (uuid_.equals(((CommAddress) o).uuid_));
  }

  @Override
  public int hashCode() {
    return uuid_.hashCode();
  }

  @Override
  public String toString() {
    return uuid_.toString();
  }

  public KeyDHT toKeyDHT() {
    return KeyDHT.fromSerializableObject(this);
  }
}
