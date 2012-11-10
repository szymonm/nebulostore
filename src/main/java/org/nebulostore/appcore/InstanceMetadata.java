package org.nebulostore.appcore;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.nebulostore.addressing.AppKey;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.IMergeable;
import org.nebulostore.networkmonitor.PeerConnectionSurvey;

/**
 * Metadata stored in DHT for Nebulostore instance.
 * @author szymonmatejczyk
 */
public class InstanceMetadata implements Serializable, IMergeable {
  private static final long serialVersionUID = -2246471507395388278L;

  /* Id of user, that this metadata applies to.*/
  private final AppKey owner_;

  CommAddress currentAddress_;
  /* Communication addresses of peers that store messages for @instance.*/
  LinkedList<InstanceID> inboxHolders_;

  private final ConcurrentLinkedQueue<PeerConnectionSurvey> statistics_ =
      new ConcurrentLinkedQueue<PeerConnectionSurvey>();

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
  public IMergeable merge(IMergeable other) {
    return this;
  }

  public ConcurrentLinkedQueue<PeerConnectionSurvey> getStatistics() {
    return statistics_;
  }

  public void removeOldStatistics() {
    // TODO(szm)
  }

  @Override
  public String toString() {
    return "InstanceMetadata: owner: " + owner_.toString() + " current address: " +
        currentAddress_.toString() + " synchro-peer num: " + inboxHolders_.size();
  }
}
