package org.nebulostore.broker;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.nebulostore.api.PutKeyModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.addressing.ReplicationGroup;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.broker.messages.ContractOfferMessage;
import org.nebulostore.broker.messages.OfferReplyMessage;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.networkmonitor.NetworkContext;

/**
 * Broker is always a singleton job. See BrokerMessageForwarder.
 * @author Bolek Kulbabinski
 */
public class AlwaysAcceptingBroker extends Broker {
  private static Logger logger_ = Logger.getLogger(AlwaysAcceptingBroker.class);

  private static final int TIMEOUT_SEC = 10;
  private static final int MAX_CONTRACTS = 3;
  private final BrokerVisitor visitor_ = new BrokerVisitor();
  private Set<CommAddress> offerRecipients_ = new TreeSet<CommAddress>();

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
      //TODO(bolek): Do we want it here?
      //NetworkContext.getInstance().addFoundPeer(message.getSourceAddress());
      //TODO(bolek): Should we accept same offer twice?
      networkQueue_.add(new OfferReplyMessage(message.getId(), message.getDestinationAddress(),
          message.getSourceAddress(), message.getContract(), true));
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
        PutKeyModule module = new PutKeyModule(currGroup, outQueue_);

        try {
          module.getResult(TIMEOUT_SEC);
        } catch (NebuloException exception) {
          logger_.warn("Unsuccessful DHT update.");
        }
      } else {
        logger_.debug("Peer " + message.getSourceAddress() + " rejected our offer.");
      }
      return null;
    }

    @Override
    public Void visit(CommPeerFoundMessage message) {
      logger_.debug("Found new peer.");
      if (BrokerContext.getInstance().getReplicas().length < MAX_CONTRACTS) {
        Vector<CommAddress> knownPeers = NetworkContext.getInstance().getKnownPeers();
        Iterator<CommAddress> iterator = knownPeers.iterator();
        while (iterator.hasNext()) {
          CommAddress address = iterator.next();
          if (BrokerContext.getInstance().getUserContracts(address) == null &&
              !address.equals(myAddress_) && !offerRecipients_.contains(address)) {
            // Send offer to new peer (10MB by default).
            logger_.debug("Sending offer to " + address);
            networkQueue_.add(new ContractOfferMessage(CryptoUtils.getRandomString(), null, address,
                new Contract("contract", myAddress_, address, 10 * 1024)));
            offerRecipients_.add(address);
            break;
          }
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
