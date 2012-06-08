package org.nebulostore.appcore;

import java.io.Serializable;
import java.util.LinkedList;

import org.nebulostore.addressing.AppKey;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.Mergeable;

/**
 * Metadata stored in DHT for Nebulostore instance.
 * @author szymonmatejczyk
 */
public class InstanceMetadata implements Serializable, Mergeable {
  private static final long serialVersionUID = -2246471507395388278L;

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

  public LinkedList<InstanceID> getInboxHolders() {
    return inboxHolders_;
  }

  public CommAddress getCurrentAddress() {
    return currentAddress_;
  }

  @Override
  public Mergeable merge(Mergeable other) {
    return this;
  }
}
