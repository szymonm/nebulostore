package org.nebulostore.testing;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;
import org.nebulostore.testing.communication.messages.PingMessage;
import org.nebulostore.testing.communication.messages.PongMessage;

/**
 * @author Grzegorz Milka 
 */
public final class PingPongPeer {
  private final Logger logger_ = Logger.getLogger(PingPongPeer.class);

  private BlockingQueue<Message> inQueue_ = new LinkedBlockingQueue<Message>();
  private BlockingQueue<Message> outQueue_ = new LinkedBlockingQueue<Message>();

  private final int peerId_;
  private Set<CommAddress> knownPeers_;
  private Collection<Integer> oldPings_;
  /**
   * Respondents to given ping
   */
  private Map<Integer, Collection<Integer> > respondents_;

  public PingPongPeer(int peerId) throws NebuloException { 
    peerId_ = peerId;
    knownPeers_ = new HashSet<CommAddress>();
    oldPings_ = new HashSet<Integer>();
    respondents_ = new HashMap<Integer, Collection<Integer> >();

    Thread listener = new Thread(
        new Listener(), "Nebulostore.Testing.PingPongPeer$Listener");
    listener.setDaemon(true);
    listener.start();

    Thread communicationPeer = new Thread(
        new CommunicationPeer(inQueue_, outQueue_), 
        "Nebulostore.Communication.CommunicationPeer");
    communicationPeer.setDaemon(true);
    communicationPeer.start();
  }

  /**
   * Returns Collection of respondents to ping of given id.
   */
  public Collection<Integer> getRespondents(int pingId) {
    Collection<Integer> respondents = respondents_.get(pingId);
    if (respondents == null) {
      respondents = new HashSet<Integer>();
    }
    return respondents;
  }

  public void sendPing(int pingId) {
    PingMessage pingMsg = new PingMessage(null, peerId_, pingId);
    oldPings_.add(pingId);
    respondents_.put(pingId, new TreeSet<Integer>());
    for (CommAddress peer: knownPeers_) {
      pingMsg.setDestinationAddress(peer);
      inQueue_.add(pingMsg);
    }
  }

  private class Listener implements Runnable{
    public void run() {
      while (true) {
        Message msg = null;
        try {
          msg = outQueue_.take();
        } catch (InterruptedException e) {
          logger_.error("Interrupt when trying to take message.");
          continue;
        }
        logger_.debug("Received msg: " + msg + ".");

        if (msg instanceof CommPeerFoundMessage) {
          CommPeerFoundMessage cPFMsg = (CommPeerFoundMessage) msg;
          logger_.info("Found new peer: " + cPFMsg.getSourceAddress());
          knownPeers_.add(cPFMsg.getSourceAddress());
        } else if (msg instanceof PingMessage) {
          PingMessage ping = (PingMessage) msg;
          if (oldPings_.contains(ping.getPingId())) {
            logger_.debug("Received old Ping message: " + ping.getPingId());
          } else {
            oldPings_.add(ping.getPingId());
            resendPing(ping);
            sendResponsePong(ping);
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
    }
  }

  private void resendPing(PingMessage pingMsg) {
    PingMessage rePingMsg = new PingMessage(null, peerId_, pingMsg.getPingId());
    for (CommAddress peer: knownPeers_) {
      rePingMsg.setDestinationAddress(peer);
      inQueue_.add(rePingMsg);
    }
  }

  private void sendResponsePong(PingMessage pingMessage) {
    PongMessage pongMsg = 
      new PongMessage(pingMessage.getSourceAddress(), peerId_, pingMessage);
    inQueue_.add(pongMsg);
  }
}
