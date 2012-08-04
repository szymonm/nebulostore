package org.nebulostore.networkmonitor;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.appcore.GlobalContext;
import org.nebulostore.appcore.Message;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.timer.IMessageGenerator;

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

  private Set<CommAddress> randomPeersSample_ = new HashSet<CommAddress>();

  /**
   * Messages to be send to dispatcher when context changes.
   */
  private final HashSet<IMessageGenerator> contextChangeMessageGenerators_ =
      new HashSet<IMessageGenerator>();

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
    for (IMessageGenerator m : contextChangeMessageGenerators_) {
      getDispatcherQueue().add(m.generate());
    }
  }

  /**
   * @deprecated Use addContextChangeMessageGenerator.
   */
  @Deprecated
  public synchronized void addContextChangeMessage(final Message message) {
    IMessageGenerator generator = new IMessageGenerator() {
      @Override
      public Message generate() {
        return message;
      }
    };
    contextChangeMessageGenerators_.add(generator);
  }
/*
  public synchronized void removeContextChangeMessage(Message message) {
    contextChangeMessagesGenerators_.remove(message);
  }
*/
  public synchronized void addContextChangeMessageGenerator(IMessageGenerator generator) {
    contextChangeMessageGenerators_.add(generator);
  }

  public void removeContextChangeMessageGenerator(IMessageGenerator generator) {
    contextChangeMessageGenerators_.remove(generator);
  }

  public Vector<CommAddress> getKnownPeers() {
    return new Vector<CommAddress>(knownPeersVector_);
  }

  public synchronized void addFoundPeer(CommAddress address) {
    // TODO(mbw): address != null, because of Broker.java:40
    if (!knownPeers_.contains(address) && address != null) {
      logger_.debug("Adding a CommAddress: " + address);
      knownPeers_.add(address);
      knownPeersVector_.add(address);

      if (randomPeersSample_.size() < RandomPeersGossipingModule.RANDOM_PEERS_SAMPLE_SIZE) {
        randomPeersSample_.add(address);
      }
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

  public Set<CommAddress> getRandomPeersSample() {
    return randomPeersSample_;
  }

  public void setRandomPeersSample(Set<CommAddress> randomPeersSample) {
    randomPeersSample_ = randomPeersSample;
  }


}
