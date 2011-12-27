package org.nebulostore.appcore;

import org.nebulostore.communication.address.CommAddress;

/**
 * @author szymonmatejczyk
 */
public class HardLink extends DirectoryEntry {

  /**
   * Temporal field for debugging.
   */
  public String title_;
  public ObjectId objectId_;
  /* Addresses of replicas of object objectKey_. */
  public CommAddress[] objectPhysicalAddresses_;

  public HardLink(String title, ObjectId objectId, CommAddress[] objectPhysicalAddresses) {
    super();
    title_ = title;
    objectId_ = objectId;
    objectPhysicalAddresses_ = objectPhysicalAddresses;
  }

  public HardLink(ObjectId objectId, CommAddress[] objectPhysicalAddresses) {
    super();
    title_ = "";
    objectId_ = objectId;
    objectPhysicalAddresses_ = objectPhysicalAddresses;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result;
    result = prime + ((objectId_ == null) ? 0 : objectId_.hashCode());
    result = prime * result +
        ((objectPhysicalAddresses_ == null) ? 0 : objectPhysicalAddresses_.hashCode());
    result = prime * result + ((title_ == null) ? 0 : title_.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    HardLink other = (HardLink) obj;
    if (objectId_ == null) {
      if (other.objectId_ != null)
        return false;
    } else if (!objectId_.equals(other.objectId_))
      return false;
    if (objectPhysicalAddresses_ == null) {
      if (other.objectPhysicalAddresses_ != null)
        return false;
    } else if (!objectPhysicalAddresses_.equals(other.objectPhysicalAddresses_))
      return false;
    if (title_ == null) {
      if (other.title_ != null)
        return false;
    } else if (!title_.equals(other.title_))
      return false;
    return true;
  }
}
