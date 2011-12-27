package org.nebulostore.appcore;

import org.nebulostore.communication.address.CommAddress;

/**
 * Soft link (to other user's file).
 */
public class SoftLink extends DirectoryEntry {
  public NebuloKey nebuloKey_;
  /* Addresses of replicas of object represented by nebuloKey_ (last element of the 'path') */
  public CommAddress[] objectPhysicalAddresses_;

  public SoftLink(NebuloKey nebuloKey, CommAddress[] objectPhysicalAddresses) {
    super();
    nebuloKey_ = nebuloKey;
    objectPhysicalAddresses_ = objectPhysicalAddresses;
  }
}
