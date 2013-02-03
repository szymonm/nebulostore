package org.nebulostore.systest.communication.pingpong;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;


/**
 * @author Grzegorz Milka
 */
public final class PingPongPeerImpl extends AbstractPeerImpl implements PingPongPeer {
  private Set<CommAddress> knownPeers_;
  private Collection<Integer> oldPings_;
  /**
   * Respondents to given ping.
   */
  private Map<Integer, Collection<Integer> > respondents_;

  public PingPongPeerImpl(int peerId) throws NebuloException {
    super(peerId);
    knownPeers_ = new HashSet<CommAddress>();
    oldPings_ = new HashSet<Integer>();
    respondents_ = new HashMap<Integer, Collection<Integer> >();
    logger_.info("Created peer of id: " + peerId_ + ".");
  }

  /**
   * Returns Collection of respondents to ping of given id.
   */
  public Collection<Integer> getRespondents(int pingId) throws RemoteException {
    Collection<Integer> respondents = respondents_.get(pingId);
    if (respondents == null) {
      respondents = new HashSet<Integer>();
    }
    return respondents;
  }

  public void sendPing(int pingId) throws RemoteException {
    oldPings_.add(pingId);
    respondents_.put(pingId, new TreeSet<Integer>());
    logger_.debug("Sending ping to: " + knownPeers_);
    for (CommAddress peer : knownPeers_) {
      PingMessage pingMsg = new PingMessage(CommunicationPeer.getPeerAddress(),
              peer, peerId_, pingId);
      inQueue_.add(pingMsg);
    }
  }

  @Override
  protected void processMessage(Message msg) {
    logger_.debug("Received msg: " + msg + ".");
    if (msg instanceof CommPeerFoundMessage) {
      CommPeerFoundMessage cPFMsg = (CommPeerFoundMessage) msg;
      logger_.debug("Found new peer: " + cPFMsg.getSourceAddress());
      knownPeers_.add(cPFMsg.getSourceAddress());
    } else if (msg instanceof PingMessage) {
      PingMessage ping = (PingMessage) msg;
      if (oldPings_.contains(ping.getPingId())) {
        logger_.debug("Received old Ping message: " + ping.getPingId());
      } else {
        oldPings_.add(ping.getPingId());
        resendPing(ping);
        logger_.debug("Responding to ping: " + msg);
        sendResponsePong(ping);
        logger_.debug("Responded to ping: " + msg);
      }
    } else if (msg instanceof PongMessage) {
      PongMessage pong = (PongMessage) msg;
      Collection<Integer> respondents = respondents_.get(pong.getPingId());
      if (respondents == null) {
        respondents = new HashSet<Integer>();
        respondents_.put(pong.getPingId(), respondents);
      }
      respondents.add(pong.getPeerId());
    } else if (msg instanceof ErrorCommMessage) {
      ErrorCommMessage errCommMsg = (ErrorCommMessage) msg;
      knownPeers_.remove(errCommMsg.getMessage().getDestinationAddress());
    }
  }

  private void resendPing(PingMessage pingMsg) {
    PingMessage rePingMsg = new PingMessage(pingMsg.getRootSourceAddress(),
            null, peerId_, pingMsg.getPingId());
    for (CommAddress peer : knownPeers_) {
      rePingMsg.setDestinationAddress(peer);
      inQueue_.add(rePingMsg);
    }
  }

  private void sendResponsePong(PingMessage pingMessage) {
    PongMessage pongMsg =
      new PongMessage(pingMessage.getRootSourceAddress(), peerId_, pingMessage);
    logger_.debug("Sending PongMessage: " + pongMsg);
    inQueue_.add(pongMsg);
  }
}
