package org.nebulostore.appcore;

import org.nebulostore.communication.address.CommAddress;

/**
 * @author szymonmatejczyk
 */
public class Reference extends DirectoryEntry {

  /**
   * Temporal field for debugging.
   */
  public String title_;
  public ObjectId objectKey_;
  /* Addresses of replicas of object objectKey_. */
  public CommAddress[] objectPhysicalAddresses_;

  public Reference(String title, ObjectId objectId,
    CommAddress[] objectPhysicalAddress) {
    super();
    title_ = title;
    objectKey_ = objectId;
    objectPhysicalAddresses_ = objectPhysicalAddress;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result;
    result = prime + ((objectKey_ == null) ? 0 : objectKey_.hashCode());
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
    Reference other = (Reference) obj;
    if (objectKey_ == null) {
      if (other.objectKey_ != null)
        return false;
    } else if (!objectKey_.equals(other.objectKey_))
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
