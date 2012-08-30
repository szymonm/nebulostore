package org.nebulostore.communication.gossip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;
import org.nebulostore.communication.messages.gossip.PeerGossipMessage;

/**
 * Standard peer gossiping service.
 *
 * Basic implementation of Gossip-based peer sampling based on
 * "Gossip-based Peer Sampling" by Jelasty, Voulgaris...
 * @author Grzegorz Milka
 */
//TODO-GM Add peer discovery
//TODO-GM Add bootstrap handling
public class PeerGossipService extends Module {
  private final Logger logger_ = Logger.getLogger(PeerGossipService.class);

  private static final Random RANDOMIZER = new Random();
  //20 seconds
  private static final int GOSSIP_PERIOD = 20000;
  /**
   * Equivalent of c in the paper.
   *
   * It has to be greater than 1 (or even 3) to ensure non-empty gossips.
   */
  private static final int MAX_PEERS_SIZE = 20;
  /**
   * Equivalent of H in the paper.
   */
  private static final int HEALING_FACTOR = 1;
  /**
   * Equivalent of S in the paper.
   */
  private static final int SWAPPING_FACTOR = 1;
  private List<PeerDescriptor> peers_ =
    Collections.synchronizedList(new LinkedList<PeerDescriptor>());
  Timer gossipSender_ = new Timer();

  private CommAddress myCommAddress_;
  /**
   * Address pointing to bootstrap server.
   */
  private CommAddress bootstrapCommAddress_;

  public PeerGossipService(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue, CommAddress myCommAddress,
      CommAddress bootstrapCommAddress) throws NebuloException {
    super(inQueue, outQueue);
    myCommAddress_ = myCommAddress;
    bootstrapCommAddress_ = bootstrapCommAddress;
    gossipSender_.schedule(new GossipSender(), GOSSIP_PERIOD, GOSSIP_PERIOD);
  }

  @Override
  public void endModule() {
    gossipSender_.cancel();
    super.endModule();
  }

  @Override
  public void processMessage(Message msg) {
    logger_.debug("Received message: " + msg + ".");
    if (msg instanceof ErrorCommMessage) {
      //Couldn't reach a peer so delete him from peers_
      CommMessage commMsg = ((ErrorCommMessage) msg).getMessage();
      synchronized (peers_) {
        Iterator<PeerDescriptor> iterator = peers_.iterator();
        while (iterator.hasNext()) {
          if (iterator.next().getPeerAddress() == commMsg.getDestinationAddress()) {
            iterator.remove();
            logger_.debug("Removed: " + commMsg.getDestinationAddress() +
                " from peer pool.");
            break;
          }
        }
      }
    } else if (msg instanceof PeerGossipMessage) {
      PeerGossipMessage peerGossipMsg = (PeerGossipMessage) msg;
      if (peerGossipMsg.getMsgType().contains(PeerGossipMessage.MessageType.PULL)) {
        outQueue_.add(prepareMsgToSend(peerGossipMsg.getSourceAddress(),
              EnumSet.of(PeerGossipMessage.MessageType.PUSH)));
      }
      if (peerGossipMsg.getMsgType().contains(PeerGossipMessage.MessageType.PUSH)) {
        select(peerGossipMsg.getBuffer());
      }

      increaseAge();
    }
  }

  /**
   * Get random active peer.
   *
   * It returns null if no peer is present.
   */
  public CommAddress getPeer() {
    PeerDescriptor peer = selectPeer();
    if (peer != null)
      return peer.getPeerAddress();
    else
      return null;
  }

  /**
   * @author Grzegorz Milka
   */
  private class GossipSender extends TimerTask {
    @Override
    public void run() {
      PeerDescriptor recipient = selectPeer();
      if (recipient == null && !bootstrapCommAddress_.equals(myCommAddress_)) {
        peers_.add(new PeerDescriptor(bootstrapCommAddress_));
        recipient = new PeerDescriptor(bootstrapCommAddress_);
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
  private class AgeComparator implements Comparator<PeerDescriptor> {
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
  //NOTE-GM: tail(oldest peer) strategy also works
  private PeerDescriptor selectPeer() {
    synchronized (peers_) {
      if (peers_.size() == 0)
        return null;
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

  private PeerGossipMessage prepareMsgToSend(CommAddress recipient,
      EnumSet <PeerGossipMessage.MessageType> msgType) {
    if (!msgType.contains(PeerGossipMessage.MessageType.PUSH)) {
      return new PeerGossipMessage(myCommAddress_, recipient, msgType,
          new LinkedList<PeerDescriptor>());
    }

    ArrayList<PeerDescriptor> bufferToSend;
    synchronized (peers_) {
      bufferToSend = new ArrayList<PeerDescriptor>(peers_);
    }
    Collections.shuffle(bufferToSend);

    // Finding H oldest address and moving them to the end of list
    if (HEALING_FACTOR < bufferToSend.size()) {
      ArrayList<PeerDescriptor> hOldest =
        new ArrayList<PeerDescriptor>(bufferToSend);
      Collections.sort(hOldest, new AgeComparator());
      hOldest.subList(0, hOldest.size() - HEALING_FACTOR - 1).clear();
      bufferToSend.removeAll(hOldest);
      bufferToSend.addAll(hOldest);
    }

    if (bufferToSend.size() > (MAX_PEERS_SIZE / 2 - 1))
      bufferToSend.subList(MAX_PEERS_SIZE / 2 - 1, bufferToSend.size() - 1).clear();

    bufferToSend.add(0, new PeerDescriptor(myCommAddress_));

    return new PeerGossipMessage(myCommAddress_, recipient, msgType,
        bufferToSend);
  }

  private void select(Collection<PeerDescriptor> otherPeers) {
    synchronized (peers_) {
      //make a copy of peers
      Set<PeerDescriptor> oldPeers = new HashSet<PeerDescriptor>(peers_);

      //Append new list
      peers_.addAll(otherPeers);
      //Remove myself if present
      peers_.remove(new PeerDescriptor(myCommAddress_));

      //Remove duplicates leaving younger descriptors
      Set<PeerDescriptor> sortedPeerSet = new TreeSet<PeerDescriptor>(new AgeComparator());
      sortedPeerSet.addAll(peers_);
      Set<PeerDescriptor> uniqPeerSet = new HashSet<PeerDescriptor>();
      //Can not assume addAll works as if moving from begin to end so i do it
      //manually.
      //Using the fact that add doesn't replace already existing element
      for (PeerDescriptor peer : sortedPeerSet) {
        uniqPeerSet.add(peer);
      }

      peers_.clear();
      peers_.addAll(uniqPeerSet);

      //Delete old items
      ArrayList<PeerDescriptor> peerList = new ArrayList<PeerDescriptor>(peers_);
      if (peers_.size() > MAX_PEERS_SIZE) {
        Collections.sort(peerList, new AgeComparator());
        peerList.subList(0, peerList.size() -
            Math.min(HEALING_FACTOR, peers_.size() - MAX_PEERS_SIZE) - 1).
          clear();
        peers_.removeAll(peerList);
      }

      //Delete head
      final int headRange =
        Math.max(Math.min(SWAPPING_FACTOR, peers_.size() - MAX_PEERS_SIZE), 0);
      for (int i = 0; i < headRange; ++i) {
        peers_.remove(0);
      }

      //Remove at random
      if (peers_.size() > MAX_PEERS_SIZE) {
        peerList.clear();
        peerList.addAll(peers_);
        Collections.shuffle(peerList);
        peerList.subList(MAX_PEERS_SIZE, peers_.size() - 1).clear();
        peers_.retainAll(peerList);
      }

      //make a copy of peers and get diff of new peers
      Set<PeerDescriptor> newPeers = new HashSet<PeerDescriptor>(peers_);
      newPeers.removeAll(oldPeers);
      logger_.debug("Size of new peers: " + newPeers.size());
      for (PeerDescriptor peer : newPeers) {
        outQueue_.add(new CommPeerFoundMessage(peer.getPeerAddress(), myCommAddress_));
      }
    }
  }
}
