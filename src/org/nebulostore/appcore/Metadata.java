package org.nebulostore.appcore;

import java.io.Serializable;

import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.ContractList;

/**
 * Metadata object is stored in main DHT. It contains data necessary for system, that cannot be
 * stored in as other files, because they are used for replica management.
 * It also stores user's ContractList.
 * @author szymonmatejczyk
 * @author bolek
 */
public class Metadata implements Serializable {
  private static final long serialVersionUID = 8900375455728664721L;

  /* Id of user, that this metadata applies to.*/
  private final AppKey owner_;

  private ContractList contractList_;

  public Metadata(AppKey owner, ContractList contractList) {
    owner_ = owner;
    contractList_ = contractList;
  }

  public AppKey getOwner() {
    return owner_;
  }

  public ContractList getContractList() {
    return contractList_;
  }

}