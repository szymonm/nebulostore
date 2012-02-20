package org.nebulostore.appcore;

import org.nebulostore.addressing.NebuloAddress;

/**
 * Soft link (to other user's file).
 */
public class SoftLink extends DirectoryEntry {
  public NebuloAddress address_;

  public SoftLink(NebuloAddress address) {
    address_ = address;
  }
}
