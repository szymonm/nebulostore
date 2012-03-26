package org.nebulostore.appcore;

import java.util.LinkedList;

import org.nebulostore.addressing.AppKey;
import org.nebulostore.communication.address.CommAddress;

/**
 * Metadata stored in DHT for Nebulostore instace.
 * @author szymonmatejczyk
 */
public class InstanceMetadata {

  /* Id of user, that this metadata applies to.*/
  private final AppKey owner_;

  CommAddress currentAddress_;
  /* Communication addresses of peers that store messages for @instance.*/
  LinkedList<InstanceID> inboxHolders_;

  public InstanceMetadata(AppKey owner, LinkedList<InstanceID> inboxHolders) {
    owner_ = owner;
    inboxHolders_  = inboxHolders;
  }

  public InstanceMetadata(AppKey owner, CommAddress currentAddress,
      LinkedList<InstanceID> inboxHolders) {
    owner_ = owner;
    currentAddress_ = currentAddress;
    inboxHolders_ = inboxHolders;
  }

  private static final long serialVersionUID = -2246471507395388278L;

  public LinkedList<InstanceID> getInboxHolders() {
    return inboxHolders_;
  }

  public CommAddress getCurrentAddress() {
    return currentAddress_;
  }
}
