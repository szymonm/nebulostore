package org.nebulostore.systest.readwrite;

import java.util.List;
import java.util.Vector;

import org.nebulostore.appcore.addressing.NebuloAddress;
import org.nebulostore.conductor.CaseStatistics;

/**
 * Statistics for read-write test.
 *
 * @author Bolek Kulbabinski
 */
public final class ReadWriteStats extends CaseStatistics {
  private static final long serialVersionUID = -2676033693308257318L;
  private List<NebuloAddress> unavailableFiles_ = new Vector<NebuloAddress>();

  public void addAddress(NebuloAddress address) {
    unavailableFiles_.add(address);
  }

  public List<NebuloAddress> getAddresses() {
    return unavailableFiles_;
  }
}
