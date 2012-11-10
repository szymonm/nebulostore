package org.nebulostore.broker;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.ReplicationGroup;
import org.nebulostore.api.PutKeyModule;
import org.nebulostore.appcore.InstanceID;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.broker.messages.ContractOfferMessage;
import org.nebulostore.broker.messages.OfferReplyMessage;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.networkmonitor.NetworkContext;
import org.nebulostore.timer.IMessageGenerator;

/**
 * @author bolek
 */
public class Broker extends JobModule {
  // TODO(bolek): Now we have one instance of Broker always running (created in Peer)
  // just to wait for CommPeerFound notifications + other instances created when some other
  // messages arrive (e.g. contract offers). Can it be cleaner?
  private static Logger logger_ = Logger.getLogger(Broker.class);

  private static final int TIMEOUT_SEC = 10;
  private final BrokerVisitor visitor_;

  public Broker(String jobId, boolean permanentInstance) {
    super(jobId);
    visitor_ = new BrokerVisitor();
    // The instance that is always running (responsible for timers etc).
    if (permanentInstance) {
      NetworkContext.getInstance().addContextChangeMessageGenerator(
          new IMessageGenerator() {
            @Override
            public Message generate() {
              return new CommPeerFoundMessage(jobId_, null, null);
            }
          });
    }
  }

  /**
   * Visitor.
   */
  private class BrokerVisitor extends MessageVisitor<Void> {
    @Override
    public Void visit(JobInitMessage message) {
      logger_.debug("Initialized");
      return null;
    }

    @Override
    public Void visit(ContractOfferMessage message) {
      // Accept every offer!
      logger_.debug("Accepting offer from: " + message.getSourceAddress());
      NetworkContext.getInstance().addFoundPeer(message.getSourceAddress());
      networkQueue_.add(new OfferReplyMessage(message.getId(), message.getDestinationAddress(),
          message.getSourceAddress(), message.getContract(), true));
      endJobModule();
      return null;
    }

    @Override
    public Void visit(OfferReplyMessage message) {
      if (message.getResult()) {
        // Offer was accepted, add new replica to our DHT entry.
        logger_.debug("Peer " + message.getSourceAddress() + " accepted our offer.");
        BrokerContext.getInstance().addContract(message.getContract());

        ReplicationGroup currGroup = new ReplicationGroup(BrokerContext.getInstance().getReplicas(),
            new BigInteger("0"), new BigInteger("1000000"));
        PutKeyModule module = new PutKeyModule(NetworkContext.getAppKey(), currGroup, outQueue_);

        try {
          module.getResult(TIMEOUT_SEC);
        } catch (NebuloException exception) {
          logger_.warn("Unsuccessful DHT update.");
        }
      } else {
        logger_.debug("Peer " + message.getSourceAddress() + " rejected our offer.");
      }
      endJobModule();
      return null;
    }

    @Override
    public Void visit(CommPeerFoundMessage message) {
      logger_.debug("Found new peer - sending offers.");
      Vector<CommAddress> knownPeers = NetworkContext.getInstance().getKnownPeers();
      Iterator<CommAddress> iterator = knownPeers.iterator();
      while (iterator.hasNext()) {
        CommAddress address = iterator.next();
        if (BrokerContext.getInstance().getUserContracts(new InstanceID(address)) == null &&
            !address.equals(CommunicationPeer.getPeerAddress())) {
          // Send offer to new peer (10MB by default).
          networkQueue_.add(new ContractOfferMessage(CryptoUtils.getRandomString(), null, address,
              new Contract("contract", new InstanceID(address), 10 * 1024)));
        }
      }
      return null;
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }
}
