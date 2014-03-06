package org.nebulostore.communication.gossip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;

import org.apache.commons.configuration.XMLConfiguration;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.appcore.modules.EndModuleMessage;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.gossip.core.PeerDescriptor;
import org.nebulostore.communication.gossip.messages.PeerGossipMessage;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;

/**
 * Standard peer gossiping service.
 *
 * Basic implementation of Gossip-based peer sampling based on
 * "Gossip-based Peer Sampling" by Jelasty, Voulgaris...
 * @author Grzegorz Milka
 */
public final class PeerSamplingGossipService extends GossipService {
  private final Logger logger_ = Logger.getLogger(PeerSamplingGossipService.class);

  private static final int DEFAULT_GOSSIP_PERIOD = 20000;
  private static final Random RANDOMIZER = new Random();
  /**
   * Period at which GossipSender sends its advertisments. In milliseconds.
   */
  private int gossipPeriod_;
  /**
   * Equivalent of c in the paper.
   *
   * It has to be greater than 1 (or even 3) to ensure non-empty gossips.
   */
  // Default: 20
  private final int maxPeersSize_ = 20;
  /**
   * Equivalent of H in the paper.
   */
  // Default: 1
  private final int healingFactor_ = 1;
  /**
   * Equivalent of S in the paper.
   */
  // Default: 5
  private final int swappingFactor_ = 5;

  private List<PeerDescriptor> peers_ =
    Collections.synchronizedList(new LinkedList<PeerDescriptor>());

  /**
   * Timer for running periodical gossip exchange.
   * true argument stands for running as a deamon thread
   */
  private final Timer gossipSender_ = new Timer(true);
  private final GossipMsgVisitor msgVisitor_ = new GossipMsgVisitor();

  @AssistedInject
  public PeerSamplingGossipService(
      XMLConfiguration config,
      @Assisted("GossipServiceInQueue") BlockingQueue<Message> inQueue,
      @Assisted("GossipServiceOutQueue") BlockingQueue<Message> outQueue,
      @Named("LocalCommAddress") CommAddress commAddress,
      @Assisted("BootstrapCommAddress") CommAddress bootstrapCommAddress) {
    super(config, inQueue, outQueue, commAddress, bootstrapCommAddress);
  }

  @Override
  protected void initModule() {
    gossipPeriod_ = config_.getInt(CONFIG_PREFIX + "gossip-period", DEFAULT_GOSSIP_PERIOD);
    // Bootstrap is already found.
    outQueue_.add(new CommPeerFoundMessage(bootstrapCommAddress_, commAddress_));
    startGossipSender();
  }

  public void shutdown() {
    stopGossipSender();
    endModule();
  }

  @Override
  public void processMessage(Message msg) {
    logger_.debug("Received message: " + msg + ".");
    try {
      msg.accept(msgVisitor_);
    } catch (NebuloException e) {
      logger_.warn("NebuloException: " + e + " occured when accepting msg: " +
          msg);
    }
  }

  /**
   * Get random active peer.
   *
   * It returns null if no peer is present.
   */
  public CommAddress getPeer() {
    PeerDescriptor peer = selectPeer();
    if (peer != null) {
      return peer.getPeerAddress();
    } else {
      return null;
    }
  }

  /**
   * TimerTask responsible for sending PUSHPULL Gossip every GOSSIP_PERIOD
   * mseconds.
   *
   * @author Grzegorz Milka
   */
  private class GossipSender extends TimerTask {
    @Override
    public void run() {
      PeerDescriptor recipient = selectPeer();
      if (recipient == null && !bootstrapCommAddress_.equals(commAddress_)) {
        synchronized (peers_) {
          peers_.add(new PeerDescriptor(bootstrapCommAddress_));
          recipient = selectPeer();
          assert recipient != null;
        }
      }

      if (recipient != null) {
        logger_.info("Sending gossip to: " + recipient.getPeerAddress());
        outQueue_.add(prepareMsgToSend(recipient.getPeerAddress(),
              EnumSet.of(
                PeerGossipMessage.MessageType.PUSH,
                PeerGossipMessage.MessageType.PULL)));
        recipient.setAge(0);
      } else {
        logger_.info("Couldn't send gossip due to lack possible recipients.");
      }
    }
  }

  /**
   * @author Grzegorz Milka
   */
  private static class AgeComparator implements Comparator<PeerDescriptor>,
          Serializable {
    static final long serialVersionUID = 5749268724700386472L;
    public int compare(PeerDescriptor a, PeerDescriptor b) {
      int diff = a.getAge() - b.getAge();
      if (diff == 0) {
        return a.getPeerAddress().toString().compareTo(
            b.getPeerAddress().toString());
      } else {
        return diff;
      }
    }
  }

  /**
   * Select peer using rand strategy.
   *
   * It returns null if no peer is present.
   */
  //NOTE(grzegorzmilka): tail(oldest peer) strategy also works
  private PeerDescriptor selectPeer() {
    synchronized (peers_) {
      if (peers_.size() == 0) {
        return null;
      }
      int index = RANDOMIZER.nextInt(peers_.size());
      return peers_.get(index);
    }
  }

  private void increaseAge() {
    synchronized (peers_) {
      for (PeerDescriptor peer : peers_) {
        peer.setAge(peer.getAge() + 1);
      }
    }
  }

  /**
   * Constructor of appriopiate gossip message to given recipient.
   *
   * @author Grzegorz Milka
   */
  private PeerGossipMessage prepareMsgToSend(CommAddress recipient,
      EnumSet <PeerGossipMessage.MessageType> msgType) {
    if (!msgType.contains(PeerGossipMessage.MessageType.PUSH)) {
      return new PeerGossipMessage(commAddress_, recipient, msgType,
          new LinkedList<PeerDescriptor>());
    }

    List<PeerDescriptor> bufferToSend;
    synchronized (peers_) {
      bufferToSend = new ArrayList<PeerDescriptor>(peers_);
    }
    Collections.shuffle(bufferToSend);

    // Finding H oldest address and moving them to the end of list
    if (healingFactor_ < bufferToSend.size()) {
      List<PeerDescriptor> hOldest =
        new ArrayList<PeerDescriptor>(bufferToSend);
      Collections.sort(hOldest, new AgeComparator());
      hOldest.subList(0, hOldest.size() - healingFactor_ - 1).clear();
      bufferToSend.removeAll(hOldest);
      bufferToSend.addAll(hOldest);
    }

    if (bufferToSend.size() > (maxPeersSize_ / 2 - 1)) {
      bufferToSend.subList(maxPeersSize_ / 2 - 1, bufferToSend.size() - 1).clear();
    }

    bufferToSend.add(0, new PeerDescriptor(commAddress_));

    return new PeerGossipMessage(commAddress_, recipient, msgType,
        bufferToSend);
  }

  /**
   * Select method from the paper.
   */
  //WARNING: select changes otherPeers.
  private void select(Collection<PeerDescriptor> otherPeers) {
    synchronized (peers_) {
      //make a copy of peers
      Set<PeerDescriptor> oldPeers = new HashSet<PeerDescriptor>(peers_);

      //Remove myself if present
      otherPeers.remove(new PeerDescriptor(commAddress_));
      //Append new list and remove duplicates leaving younger descriptors.
      //Assuming no duplicates in otherPeers.
      for (PeerDescriptor peer : otherPeers) {
        int index = peers_.indexOf(peer);
        if (index != -1) {
          PeerDescriptor duplicatePeer = peers_.get(index);
          if (duplicatePeer.getAge() > peer.getAge()) {
            peers_.remove(index);
          } else {
            continue;
          }
        }
        peers_.add(peer);
      }

      //Delete old items
      List<PeerDescriptor> peerList = new ArrayList<PeerDescriptor>(peers_);
      if (peers_.size() > maxPeersSize_) {
        Collections.sort(peerList, new AgeComparator());
        peerList.subList(0, peerList.size() -
            Math.min(healingFactor_, peers_.size() - maxPeersSize_) - 1).
          clear();
        peers_.removeAll(peerList);
      }

      //Delete head
      //Note: Head in the paper refers to the end of list as implemented here
      final int headRange =
        Math.max(Math.min(swappingFactor_, peers_.size() - maxPeersSize_), 0);
      for (int i = 0; i < headRange; ++i) {
        peers_.remove(peers_.size() - 1);
      }

      //Remove at random
      if (peers_.size() > maxPeersSize_) {
        peerList.clear();
        peerList.addAll(peers_);
        Collections.shuffle(peerList);
        peerList.subList(maxPeersSize_, peers_.size() - 1).clear();
        peers_.retainAll(peerList);
      }

      //make a copy of peers and get diff of new peers
      Set<PeerDescriptor> newPeers = new HashSet<PeerDescriptor>(peers_);
      newPeers.removeAll(oldPeers);
      logger_.debug("Size of new peers: " + newPeers.size());
      for (PeerDescriptor peer : newPeers) {
        outQueue_.add(new CommPeerFoundMessage(peer.getPeerAddress(), commAddress_));
      }
    }
  }

  private void startGossipSender() {
    gossipSender_.schedule(new GossipSender(), gossipPeriod_, gossipPeriod_);
  }

  private void stopGossipSender() {
    gossipSender_.cancel();
  }

  /**
   * Message Visitor for PeerSamplingGossipService.
   *
   * @author Grzegorz Milka
   */
  protected class GossipMsgVisitor extends MessageVisitor<Void> {
    public Void visit(EndModuleMessage msg) {
      logger_.info("Received EndModule message");
      shutdown();
      return null;
    }

    public Void visit(ErrorCommMessage msg) {
      CommMessage commMsg = msg.getMessage();
      synchronized (peers_) {
        if (peers_.remove(new PeerDescriptor(commMsg.getDestinationAddress()))) {
          logger_.debug("Removed: " + commMsg.getDestinationAddress() +
              " from peer pool.");
        }
      }
      return null;
    }

    public Void visit(PeerGossipMessage msg) {
      PeerGossipMessage peerGossipMsg = (PeerGossipMessage) msg;
      if (peerGossipMsg.getMsgType().contains(PeerGossipMessage.MessageType.PULL)) {
        outQueue_.add(prepareMsgToSend(peerGossipMsg.getSourceAddress(),
              EnumSet.of(PeerGossipMessage.MessageType.PUSH)));
      }
      if (peerGossipMsg.getMsgType().contains(PeerGossipMessage.MessageType.PUSH)) {
        select(peerGossipMsg.getBuffer());
      }

      increaseAge();
      return null;
    }
  }
}
