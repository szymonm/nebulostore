package org.nebulostore.addressing;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import org.nebulostore.communication.address.CommAddress;

/**
 * A list of addresses of peers that share the same contract.
 */
public class ReplicationGroup implements Serializable, Comparable<ObjectId>, Iterable<CommAddress> {
  private static final long serialVersionUID = -2519006213860783596L;

  private final ArrayList<CommAddress> replicators_;

  // This group replicates objects with keys in [lowerBound_, upperBound_).
  private final BigInteger lowerBound_;
  private final BigInteger upperBound_;

  public ReplicationGroup(CommAddress[] replicators, BigInteger lBound,
      BigInteger uBound) {
    replicators_ = new ArrayList<CommAddress>(Arrays.asList(replicators));
    lowerBound_ = lBound;
    upperBound_ = uBound;
  }

  public int getSize() {
    return replicators_.size();
  }

  public CommAddress getReplicator(int index) {
    return replicators_.get(index);
  }

  public Iterator<CommAddress> iterator() {
    return replicators_.iterator();
  }

  @Override
  public int compareTo(ObjectId id) {
    if (lowerBound_.compareTo(id.getKey()) == 1) {
      // Our interval is greater than objectId.
      return 1;
    } else if (upperBound_.compareTo(id.getKey()) <= 0) {
      // Our interval is less than objectId.
      return -1;
    } else {
      // This replication group is responsible for this objectId.
      return 0;
    }
  }

  /**
   * Interval comparator. Return 0 for overlapping intervals.
   */
  static class IntervalComparator implements Comparator<ReplicationGroup>, Serializable {
    private static final long serialVersionUID = -5114789759938376551L;

    @Override
    public int compare(ReplicationGroup g1, ReplicationGroup g2) {
      if (g1.upperBound_.compareTo(g2.lowerBound_) <= 0) {
        return -1;
      } else if (g1.lowerBound_.compareTo(g2.upperBound_) >= 0) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  @Override
  public String toString() {
    return " ReplicationGroup [ lowerBoud_: " + lowerBound_ +
        ", upperBound_: " + upperBound_ + ", replicators_: " + replicators_ +
        " ] ";

  }
}
