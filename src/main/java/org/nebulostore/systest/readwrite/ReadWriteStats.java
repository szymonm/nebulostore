package org.nebulostore.systest.readwrite;

import java.util.Vector;

import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.conductor.CaseStatistics;

/**
 * Statistics for read-write test.
 * @author bolek
 */
public final class ReadWriteStats extends CaseStatistics {
  private static final long serialVersionUID = -2676033693308257318L;
  private Vector<NebuloAddress> unavailableFiles_ = new Vector<NebuloAddress>();

  public void addAddress(NebuloAddress address) {
    unavailableFiles_.add(address);
  }

  public Vector<NebuloAddress> getAddresses() {
    return unavailableFiles_;
  }
}
