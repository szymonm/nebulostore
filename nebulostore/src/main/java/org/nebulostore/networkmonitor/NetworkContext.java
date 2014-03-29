package org.nebulostore.networkmonitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.communication.naming.CommAddress;
import org.nebulostore.timer.MessageGenerator;

/**
 * Context that stores information about known peers and network state.
 *
 * Module that wants to use notifications should set dispatcher queue first!
 * @author szymonmatejczyk
 */
public final class NetworkContext {
  private static Logger logger_ = Logger.getLogger(NetworkContext.class);
  private static NetworkContext instance_;

  private final Set<CommAddress> knownPeers_;
  private final List<CommAddress> knownPeersList_;

  private Set<CommAddress> randomPeersSample_ = new HashSet<CommAddress>();

  /**
   * Messages to be send to dispatcher when context changes.
   */
  private final Set<MessageGenerator> contextChangeMessageGenerators_ =
      new HashSet<MessageGenerator>();
  private BlockingQueue<Message> dispatcherQueue_;

  public static synchronized NetworkContext getInstance() {
    if (instance_ == null) {
      instance_ = new NetworkContext();
    }
    return instance_;
  }

  private NetworkContext() {
    knownPeers_ = new HashSet<CommAddress>();
    knownPeersList_ = new ArrayList<CommAddress>();
  }

  public void setCommAddress(CommAddress commAddress) {
    knownPeers_.add(commAddress);
    knownPeersList_.add(commAddress);
  }

  private void contextChanged() {
    for (MessageGenerator m : contextChangeMessageGenerators_) {
      dispatcherQueue_.add(m.generate());
    }
  }

  /**
   * @deprecated Use addContextChangeMessageGenerator.
   */
  @Deprecated
  public synchronized void addContextChangeMessage(final Message message) {
    MessageGenerator generator = new MessageGenerator() {
      @Override
      public Message generate() {
        return message;
      }
    };
    contextChangeMessageGenerators_.add(generator);
  }

  public synchronized void addContextChangeMessageGenerator(MessageGenerator generator) {
    contextChangeMessageGenerators_.add(generator);
  }

  public void removeContextChangeMessageGenerator(MessageGenerator generator) {
    contextChangeMessageGenerators_.remove(generator);
  }

  public List<CommAddress> getKnownPeers() {
    return new ArrayList<CommAddress>(knownPeersList_);
  }

  public synchronized void addFoundPeer(CommAddress address) {
    // TODO(mbw): address != null, because of Broker.java:40
    if (!knownPeers_.contains(address) && address != null) {
      logger_.debug("Adding a CommAddress: " + address);
      knownPeers_.add(address);
      knownPeersList_.add(address);

      if (randomPeersSample_.size() < RandomPeersGossipingModule.RANDOM_PEERS_SAMPLE_SIZE) {
        randomPeersSample_.add(address);
      }
      contextChanged();
    }
  }

  public void setDispatcherQueue(BlockingQueue<Message> dispatcherQueue) {
    dispatcherQueue_ = dispatcherQueue;
  }

  public Set<CommAddress> getRandomPeersSample() {
    return randomPeersSample_;
  }

  public void setRandomPeersSample(Set<CommAddress> randomPeersSample) {
    randomPeersSample_ = randomPeersSample;
  }
}
