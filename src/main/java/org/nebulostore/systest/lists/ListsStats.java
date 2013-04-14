package org.nebulostore.systest.lists;

import java.util.Vector;

import org.nebulostore.appcore.addressing.NebuloAddress;
import org.nebulostore.conductor.CaseStatistics;

/**
 * Statistics for lists test.
 *
 * @author Bolek Kulbabinski
 */
public final class ListsStats extends CaseStatistics {
  private static final long serialVersionUID = -2676033693308257318L;
  private int nTriedFiles_;
  private Vector<NebuloAddress> unavailableFiles_ = new Vector<NebuloAddress>();

  public void incTriedFiles() {
    ++nTriedFiles_;
  }

  public int getNTriedFiles() {
    return nTriedFiles_;
  }

  public void addAddress(NebuloAddress address) {
    unavailableFiles_.add(address);
  }

  public Vector<NebuloAddress> getAddresses() {
    return unavailableFiles_;
  }
}
