package org.nebulostore.broker;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
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

  private BlockingQueue<Message> dispatcherQueue_;

  private HashSet<CommAddress> knownPeers_;

  //TODO(szm): complete events module
  /**
   * Messages to be send to dispatcher when context changes.
   */
  private HashSet<Message> contextChangeMessages_ = new HashSet<Message>();

  public static NetworkContext getInstance() {
    if (instance_ == null)
      instance_ = new NetworkContext();
    return instance_;
  }

  private NetworkContext() {
    knownPeers_ = new HashSet<CommAddress>();
    knownPeers_.add(CommunicationPeer.getPeerAddress());
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

  public HashSet<CommAddress> getKnownPeers() {
    return knownPeers_;
  }

  protected void addFoundPeer(CommAddress address) {
    knownPeers_.add(address);
    contextChanged();
  }

  private BlockingQueue<Message> getDispatcherQueue() {
    if (dispatcherQueue_ == null) {
      logger_.error("Dispatcher queue not set up.");
    }
    return dispatcherQueue_;
  }

  public void setDispatcherQueue(BlockingQueue<Message> dispatcherQueue) {
    dispatcherQueue_ = dispatcherQueue;
  }
}
