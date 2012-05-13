package org.nebulostore.broker;

import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.appcore.GlobalContext;
import org.nebulostore.appcore.Message;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;

/**
 * Context that stores information about known peers and network state.
 *
 * Module that wants to use notifications should set dispatcher queue first!
 * @author szymonmatejczyk
 */
public final class NetworkContext {
  private static Logger logger_ = Logger.getLogger(NetworkContext.class);
  private static NetworkContext instance_;
  private static AppKey appKey_;

  private final HashSet<CommAddress> knownPeers_;
  private final Vector<CommAddress> knownPeersVector_;

  //TODO(szm): complete events module
  /**
   * Messages to be send to dispatcher when context changes.
   */
  private final HashSet<Message> contextChangeMessages_ = new HashSet<Message>();

  public static NetworkContext getInstance() {
    if (instance_ == null)
      instance_ = new NetworkContext();
    return instance_;
  }

  private NetworkContext() {
    knownPeers_ = new HashSet<CommAddress>();
    knownPeers_.add(CommunicationPeer.getPeerAddress());
    knownPeersVector_ = new Vector<CommAddress>();
    knownPeersVector_.add(CommunicationPeer.getPeerAddress());
  }

  private void contextChanged() {
    for (Message m : contextChangeMessages_) {
      getDispatcherQueue().add(m);
    }
  }

  public void addContextChangeMessage(Message message) {
    contextChangeMessages_.add(message);
  }

  public void removeContextChangeMessage(Message message) {
    contextChangeMessages_.remove(message);
  }

  public Vector<CommAddress> getKnownPeers() {
    return knownPeersVector_;
  }

  protected void addFoundPeer(CommAddress address) {
    // TODO(mbw): address != null, because of Broker.java:40
    if (!knownPeers_.contains(address) && address != null) {
      logger_.debug("Adding a CommAddress: " + address);
      knownPeers_.add(address);
      knownPeersVector_.add(address);
      contextChanged();
    }
  }

  public void setAppKey(AppKey appKey) {
    appKey_ = appKey;
  }

  public static AppKey getAppKey() {
    return appKey_;
  }

  private BlockingQueue<Message> getDispatcherQueue() {
    if (GlobalContext.getInstance().getDispatcherQueue() == null) {
      logger_.error("Dispatcher queue not set up.");
    }
    return GlobalContext.getInstance().getDispatcherQueue();
  }
}
