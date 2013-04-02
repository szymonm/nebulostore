package org.nebulostore.appcore.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.subscription.model.Subscribers;
import org.nebulostore.subscription.model.SubscriptionNotification;
import org.nebulostore.subscription.modules.NotifySubscribersModule;

import static org.nebulostore.subscription.model.SubscriptionNotification.NotificationReason;

/**
 * NebuloObject - object that is stored in replicas and identified by NebuloAddress
 * (currently NebuloFile, NebuloList or FileChunk).
 * @author bolek
 */
public abstract class NebuloObject implements Serializable {
  private static final long serialVersionUID = 7791201890856369839L;

  // TODO(bolek): Is this constant more global?
  protected static final int TIMEOUT_SEC = 60;

  protected final NebuloAddress address_;
  protected transient CommAddress sender_;

  protected transient Set<String> previousVersions_;
  protected Subscribers subscribers_;
  protected transient BlockingQueue<Message> dispatcherQueue_;

  protected transient Provider<ObjectGetter> objectGetterProvider_;
  protected transient Provider<ObjectWriter> objectWriterProvider_;
  protected transient Provider<ObjectDeleter> objectDeleterProvider_;

  protected NebuloObject(NebuloAddress address) {
    address_ = address;
    subscribers_ = new Subscribers();
    previousVersions_ = new HashSet<String>();
    objectGetterProvider_ = null;
    objectWriterProvider_ = null;
    objectDeleterProvider_ = null;
  }

  @Inject
  public void setProviders(Provider<ObjectGetter> objectGetterProvider,
                           Provider<ObjectWriter> objectWriterProvider,
                           Provider<ObjectDeleter> objectDeleterProvider) {
    objectGetterProvider_ = objectGetterProvider;
    objectWriterProvider_ = objectWriterProvider;
    objectDeleterProvider_ = objectDeleterProvider;
  }

  @Inject
  public void setDispatcherQueue(@Named("DispatcherQueue") BlockingQueue<Message> queue) {
    dispatcherQueue_ = queue;
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

  public void newVersionCommitted(String version) {
    previousVersions_.add(version);
  }

  public void subscribe() throws NebuloException {
    CommAddress myAddress = getInstanceCommAddress();
    boolean subscribersExpanded = subscribers_.addSubscriber(myAddress);
    if (subscribersExpanded) {
      runSync();
    }
  }

  public void removeSubscription() throws NebuloException {
    CommAddress myAddress = getInstanceCommAddress();
    boolean subscribersChanged = subscribers_.removesSubscriber(myAddress);
    if (subscribersChanged) {
      runSync();
    }
  }

  protected void notifySubscribers(NotificationReason notificationReason) {
    if (isNotificationNecessary()) {
      CommAddress applicationAddress = getInstanceCommAddress();
      SubscriptionNotification notification =
          new SubscriptionNotification(address_, notificationReason);
      new NotifySubscribersModule(applicationAddress, dispatcherQueue_,
          notification, subscribers_.getSubscribersAddresses());
      //We don't need to wait  for a ack. Async msgs will be send automatically
    }
  }

  private CommAddress getInstanceCommAddress() {
    return CommunicationPeer.getPeerAddress();
  }

  private boolean isNotificationNecessary() {
    Set<CommAddress> addresses = subscribers_.getSubscribersAddresses();
    return !addresses.isEmpty();
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    previousVersions_ = new HashSet<String>();
  }
}
