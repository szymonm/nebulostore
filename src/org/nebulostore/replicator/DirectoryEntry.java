package org.nebulostore.replicator;

import java.io.Serializable;

import org.nebulostore.appcore.ObjectId;
import org.nebulostore.appcore.TransportLayerAddress;

/**
 * @author szymonmatejczyk
 */
public class DirectoryEntry implements Serializable {

  /**
   * Temporal field for debugging.
   */
  public String title_;
  public ObjectId objectKey_;
  public TransportLayerAddress objectPhysicalAddress_;

  public DirectoryEntry(String title, ObjectId objectId,
    TransportLayerAddress objectPhysicalAddress) {
    super();
    title_ = title;
    objectKey_ = objectId;
    objectPhysicalAddress_ = objectPhysicalAddress;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result;
    result = prime + ((objectKey_ == null) ? 0 : objectKey_.hashCode());
    result =
        prime * result + ((objectPhysicalAddress_ == null) ? 0 : objectPhysicalAddress_.hashCode());
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
    DirectoryEntry other = (DirectoryEntry) obj;
    if (objectKey_ == null) {
      if (other.objectKey_ != null)
        return false;
    } else if (!objectKey_.equals(other.objectKey_))
      return false;
    if (objectPhysicalAddress_ == null) {
      if (other.objectPhysicalAddress_ != null)
        return false;
    } else if (!objectPhysicalAddress_.equals(other.objectPhysicalAddress_))
      return false;
    if (title_ == null) {
      if (other.title_ != null)
        return false;
    } else if (!title_.equals(other.title_))
      return false;
    return true;
  }
}
