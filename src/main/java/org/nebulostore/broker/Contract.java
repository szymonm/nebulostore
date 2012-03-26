package org.nebulostore.broker;

import org.nebulostore.appcore.InstanceID;

/**
 * @author szymonmatejczyk
 * Contract between peers.
 */
public class Contract {
  private String contractId_;
  private InstanceID peer_;

  public String getContractId() {
    return contractId_;
  }

  public InstanceID getPeer() {
    return peer_;
  }

  public Contract(String contractId, InstanceID peer) {
    super();
    contractId_ = contractId;
    peer_ = peer_;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result +
        ((contractId_ == null) ? 0 : contractId_.hashCode());
    result = prime * result + ((peer_ == null) ? 0 : peer_.hashCode());
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
    Contract other = (Contract) obj;
    if (contractId_ == null) {
      if (other.contractId_ != null)
        return false;
    } else if (!contractId_.equals(other.contractId_))
      return false;
    if (peer_ == null) {
      if (other.peer_ != null)
        return false;
    } else if (!peer_.equals(other.peer_))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Contract [contractId=" + contractId_ + ", instanceID_=" + peer_ + "]";
  }

}
