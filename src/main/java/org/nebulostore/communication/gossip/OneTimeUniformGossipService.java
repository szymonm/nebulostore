package org.nebulostore.communication.gossip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;

import org.apache.commons.configuration.XMLConfiguration;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.EndModuleMessage;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.gossip.messages.PeerGossipMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Gossip service that ensures uniform gossiping between peers. It is parameterized by:
 * <b>N</b> - number of peers in the system, excluding server, and <b>K</b> - number of addresses
 * that every peer will receive. It works as follows.
 *
 * <ol>
 * <li>Bootstrap peer acts as a server. It waits for other peers to contact him with gossip
 *     messages.</li>
 * <li>Other peers (clients) send gossip messages to server with single address inside.</li>
 * <li>After server has received information from all N peers, it sends K random contact addresses
 * to every peer, ensuring uniform load.</li>
 * <li>Server also sends CommPeerFound messages to itself so broker should account for them.</li>
 * </ol>
 *
 * @author Bolek Kulbabinski
 */
public class OneTimeUniformGossipService extends GossipService {
  private static Logger logger_ = Logger.getLogger(OneTimeUniformGossipService.class);
  private static final long RANDOM_SEED = 12390L;

  /**
   * Server.
   * @author Bolek Kulbabinski
   */
  protected class ServerVisitor extends MessageVisitor<Void> {
    private List<CommAddress> received_ = new ArrayList<CommAddress>();

    public Void visit(PeerGossipMessage message) throws NebuloException {
      CommAddress peerAddress = message.getBuffer().get(0).getPeerAddress();
      logger_.debug("Received PeerGossipMessage from " + peerAddress + ".");
      received_.add(peerAddress);
      outQueue_.add(new CommPeerFoundMessage(peerAddress, commAddress_));
      if (received_.size() == nPeers_) {
        Collections.shuffle(received_, new Random(RANDOM_SEED));
        int nReplicators = Math.min(nReplicators_, nPeers_ - 1);
        for (int i = 0; i < nPeers_; i++) {
          List<PeerDescriptor> group = new ArrayList<PeerDescriptor>();
          for (int j = 1; j < nReplicators; j++) {
            group.add(new PeerDescriptor(received_.get((i + j) % nPeers_)));
          }
          outQueue_.add(new PeerGossipMessage(null, received_.get(i),
              Collections.singleton(PeerGossipMessage.MessageType.PUSH), group));
        }
      }
      return null;
    }

    public Void visit(EndModuleMessage message) throws NebuloException {
      endModule();
      return null;
    }
  }

  /**
   * Client waits for a single message from server containing peer's view.
   * @author Bolek Kulbabinski
   */
  protected class ClientVisitor extends MessageVisitor<Void> {
    public Void visit(PeerGossipMessage message) throws NebuloException {
      for (PeerDescriptor peer : message.getBuffer()) {
        outQueue_.add(new CommPeerFoundMessage(peer.getPeerAddress(), commAddress_));
      }
      return null;
    }

    public Void visit(EndModuleMessage message) throws NebuloException {
      endModule();
      return null;
    }
  }

  private MessageVisitor<Void> visitor_;
  private int nPeers_;
  private int nReplicators_;

  /**
   * @param nPeers Number of peers server is waiting for.
   * @param nReplicators Number of other peers' addresses that every peer will receive.
   */
  @AssistedInject
  public OneTimeUniformGossipService(
      XMLConfiguration config,
      @Assisted("GossipServiceInQueue") BlockingQueue<Message> inQueue,
      @Assisted("GossipServiceOutQueue") BlockingQueue<Message> outQueue,
      @Named("LocalCommAddress") CommAddress commAddress,
      @Assisted("BootstrapCommAddress") CommAddress bootstrapCommAddress,
      @Named("systest.num-test-participants") int nPeers,
      @Named("communication.one-time-uniform-gossip-n-replicators") int nReplicators) {
    super(config, inQueue, outQueue, commAddress, bootstrapCommAddress);
    nPeers_ = nPeers;
    nReplicators_ = nReplicators;
  }

  @Override
  protected void initModule() {
    checkNotNull(commAddress_);
    checkNotNull(bootstrapCommAddress_);
    if (bootstrapCommAddress_.equals(commAddress_)) {
      logger_.debug("Initializing OneTimeUniformGossipService as server.");
      visitor_ = new ServerVisitor();
    } else {
      logger_.debug("Initializing OneTimeUniformGossipService as client.");
      visitor_ = new ClientVisitor();
      sendMyAddressToServer();
    }
  }

  private void sendMyAddressToServer() {
    outQueue_.add(new PeerGossipMessage(commAddress_, bootstrapCommAddress_,
        Collections.singleton(PeerGossipMessage.MessageType.PUSH),
        Collections.singletonList(new PeerDescriptor(commAddress_))));
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }
}
