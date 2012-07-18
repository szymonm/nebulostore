package org.nebulostore.communication.address;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @author Marcin Walas
 * @author Grzegorz Milka
 */
public class CommAddress implements Serializable {
  private final UUID uuid_;
  /**
   * Placeholder
   */
  private static CommAddress zeroCommAddress_ = null;

  private CommAddress(long mostSigBits, long leastSigBits) {
    uuid_ = new UUID(mostSigBits, leastSigBits);
  }

  public CommAddress() {
    uuid_ = UUID.randomUUID();
  }

  /**
   * Return CommAddress symbolizing any or none address depending on context.
   */
  public static CommAddress getZero(){
    if(zeroCommAddress_ == null) {
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
}
