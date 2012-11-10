package org.nebulostore.broker;

import java.io.Serializable;

import org.nebulostore.appcore.InstanceID;

/**
 * Contract between peers.
 * NOTE: for now contracts are one-sided, i.e. accepting the contract means agreeing to replicate
 * sb's files.
 * @author szymonmatejczyk
 */
public class Contract implements Serializable {
  private static final long serialVersionUID = 8104248584725231818L;

  private String contractId_;
  private InstanceID peer_;
  private int sizeKb_;

  public Contract(String contractId, InstanceID peer, int sizeKb) {
    contractId_ = contractId;
    peer_ = peer;
    sizeKb_ = sizeKb;
  }

  public String getContractId() {
    return contractId_;
  }

  public InstanceID getPeer() {
    return peer_;
  }

  public int getSize() {
    return sizeKb_;
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
    if (sizeKb_ != other.sizeKb_) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Contract [contractId=" + contractId_ + ", instanceID_=" + peer_ + "]";
  }
}
