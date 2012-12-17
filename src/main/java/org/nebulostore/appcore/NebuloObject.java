package org.nebulostore.appcore;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.api.GetNebuloObjectModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.subscription.model.Subscribers;
import org.nebulostore.subscription.model.SubscriptionNotification;
import org.nebulostore.subscription.modules.NotifySubscribersModule;

/**
 * NebuloObject - object that is stored in replicas and identified by NebuloAddress
 * (currently NebuloFile, NebuloList or FileChunk).
 * @author bolek
 */
public abstract class NebuloObject implements Serializable {
  private static final long serialVersionUID = 7791201890856369839L;
  private static Logger logger_ = Logger.getLogger(NebuloObject.class);

  // TODO(bolek): Is this constant more global?
  protected static final int TIMEOUT_SEC = 60;
  protected static BlockingQueue<Message> dispatcherQueue_;

  // TODO(bolek): final?
  protected NebuloAddress address_;
  protected transient CommAddress sender_;

  protected transient String lastCommittedVersion_;
  protected transient Set<String> previousVersions_ = new HashSet<String>();
  protected Subscribers subscribers_;


  public static void initObjectApi(BlockingQueue<Message> queue) {
    dispatcherQueue_ = queue;
  }

  public static NebuloObject fromAddress(NebuloAddress key) throws NebuloException {
    // Create a handler and run it through dispatcher.
    GetNebuloObjectModule module = new GetNebuloObjectModule(key, dispatcherQueue_);
    // Exception from getResult() is simply passed to the user.
    return module.getResult(TIMEOUT_SEC);
  }

  protected NebuloObject(NebuloAddress address) {
    address_ = address;
    subscribers_ = new Subscribers();
  }

  public NebuloAddress getAddress() {
    return address_;
  }

  public void setSender(CommAddress sender) {
    sender_ = sender;
  }

  public Set<String> getVersions() {
    return previousVersions_;
  }

  public void setVersions(Set<String> versions) {
    previousVersions_ = versions;
  }

  /**
   * Commits all operations - invoked by user.
   * @throws NebuloException
   */
  public void sync() throws NebuloException {
    // TODO(bolek): return type? exception? sync/async?
    runSync();
  }

  protected abstract void runSync() throws NebuloException;

  public abstract void delete() throws NebuloException;

  public String getLastCommittedVersion() {
    return lastCommittedVersion_;
  }

  public void setLastCommittedVersion(String lastCommittedVersion) {
    lastCommittedVersion_ = lastCommittedVersion;
  }

  public void newVersionCommitted(String version) {
    previousVersions_.add(lastCommittedVersion_);
    lastCommittedVersion_ = version;
  }

  protected void notifySubscribers() {
    if (isNotificationNecessary()) {
      CommAddress applicationAddress = CommunicationPeer.getPeerAddress();
      SubscriptionNotification notification =
          new SubscriptionNotification(address_,
              SubscriptionNotification.NotificationReason.FILE_CHANGED);
      new NotifySubscribersModule(applicationAddress, dispatcherQueue_,
          notification, subscribers_.getSubscribersAddresses());
      //We don't need to wait  for a ack. Async msgs will be send automatically
    }
  }

  private boolean isNotificationNecessary() {
    Set<CommAddress> addresses = subscribers_.getSubscribersAddresses();
    return !addresses.isEmpty();
  }

  public void subscribe() throws NebuloException {
    CommAddress myAddress = CommunicationPeer.getPeerAddress();
    boolean subscribersExpands = subscribers_.addSubscriber(myAddress);
    if (subscribersExpands) {
      runSync();
    }
  }

}
