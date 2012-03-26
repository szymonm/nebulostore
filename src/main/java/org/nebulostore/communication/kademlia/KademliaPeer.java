package org.nebulostore.communication.kademlia;

import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.ID;
import net.jxta.impl.protocol.PipeAdv;
import net.jxta.peer.PeerID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.ValueDHT;
import org.nebulostore.communication.exceptions.CommException;
import org.nebulostore.communication.jxta.PeerDiscoveryService;
import org.nebulostore.communication.messages.dht.DHTMessage;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.communication.messages.kademlia.KademliaMessage;
import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Kademlia;
import org.planx.xmlstore.routing.RoutingException;

/**
 * Internal Module wrapper for DHT based on Kademlia functionlity.
 *
 * @author Marcin Walas
 *
 */
public class KademliaPeer extends Module implements DiscoveryListener {

  private static Logger logger_ = Logger.getLogger(KademliaPeer.class);

  private static final String KADEMLIA_BOOTSTRAP_ADV_ID_STR = "urn:jxta:" +
      "uuid-59616261646162614E504720503250338944BCED387C4A2BBD8E9411B78C28FF04";

  private static final int MAX_BOOTSTRAP_COUNT = 3;

  private final BlockingQueue<Message> jxtaInQueue_;
  private final CommAddress peerAddress_;

  private final BlockingQueue<Message> kademliaQueue_;
  private final BlockingQueue<Message> workerInQueue_;
  private Kademlia kademlia_;

  private int bootstrapCount_;

  public KademliaPeer(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue,
      PeerDiscoveryService peerDiscoveryService, CommAddress peerAddress,
      BlockingQueue<Message> jxtaInQueue) throws NebuloException {
    super(inQueue, outQueue);

    jxtaInQueue_ = jxtaInQueue;
    kademliaQueue_ = new LinkedBlockingQueue<Message>();
    workerInQueue_ = new LinkedBlockingQueue<Message>();
    peerAddress_ = peerAddress;
    bootstrapCount_ = 0;

    try {
      kademlia_ = new Kademlia(null, jxtaInQueue, kademliaQueue_, peerAddress);
    } catch (RoutingException e) {
      logger_.error(e);
      throw new NebuloException(e);
    } catch (IOException e) {
      logger_.error(e);
      throw new NebuloException(e);
    }

    peerDiscoveryService.addAdvertisement(getKademliaAdvertisement());
    peerDiscoveryService.getDiscoveryService().addDiscoveryListener(this);

    new Thread(new MessageWorker(workerInQueue_, this),
        "Nebulostore.Communication.DHT.KademliaMsgWorker").start();

  }

  @Override
  protected void processMessage(Message msg) throws NebuloException {
    if (msg instanceof KademliaMessage) {
      kademliaQueue_.add(msg);
      return;
    }

    if (msg instanceof DHTMessage) {
      workerInQueue_.add(msg);
      return;
    }

    logger_.error("Message of type " + msg.getClass().toString() +
        " should not be handled here");
  }


  private void get(GetDHTMessage msg) {
    ValueDHT val = null;
    try {
      Identifier keyId = new Identifier(msg.getKey().getBigInt());
      logger_.info("get of key: " + keyId);
      val = (ValueDHT) kademlia_.get(keyId);
      outQueue_.add(new ValueDHTMessage(msg, msg.getKey(), val));
    } catch (IOException e) {
      logger_.error(e);
      outQueue_.add(new ErrorDHTMessage(msg, new CommException(e)));
    }
  }

  private void put(PutDHTMessage msg) {
    Identifier keyId = new Identifier(msg.getKey().getBigInt());
    try {
      logger_.info("put on key: " + keyId);
      kademlia_.put(keyId, msg.getValue());

      logger_.info("put on key: " + keyId + " finished");
      outQueue_.add(new OkDHTMessage(msg));
    } catch (IOException e) {
      logger_.error(e);
      outQueue_.add(new ErrorDHTMessage(msg, new CommException(e)));
    }
  }

  /**
   * Internal class used to handle external put/get requests from other modules.
   * Introduced because of the fact that queue for incoming messages in KademliaPeer
   * is collecting operations from modules as well as network reponses from remote
   * hosts.
   *
   * @author Marcin Walas
   *
   */
  class MessageWorker extends Module {

    KademliaPeer kademliaPeer_;

    public MessageWorker(BlockingQueue<Message> inQueue, KademliaPeer kdPeer) {
      super(inQueue, null);
      kademliaPeer_ = kdPeer;
    }

    @Override
    protected void processMessage(Message msg) {

      if (msg instanceof PutDHTMessage) {
        kademliaPeer_.put((PutDHTMessage) msg);
        return;
      }

      if (msg instanceof GetDHTMessage) {
        kademliaPeer_.get((GetDHTMessage) msg);
        return;
      }

    }

  }

  public PipeAdvertisement getKademliaAdvertisement() {
    PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
        .newAdvertisement(PipeAdvertisement.getAdvertisementType());

    advertisement
    .setPipeID(ID.create(URI.create(KADEMLIA_BOOTSTRAP_ADV_ID_STR)));
    advertisement.setDescription(peerAddress_.getPeerId().toString());
    advertisement.setType(PipeService.UnicastType);
    advertisement.setName("Nebulostore bdb dht holder");
    advertisement.setType(KADEMLIA_BOOTSTRAP_ADV_ID_STR);
    return advertisement;
  }

  @Override
  public void discoveryEvent(DiscoveryEvent ev) {
    logger_.debug("DiscoveryEvent: " + ev.getQueryID());

    Advertisement adv;
    Enumeration<Advertisement> en = ev.getResponse().getAdvertisements();

    if (en != null) {
      while (en.hasMoreElements()) {
        adv = en.nextElement();
        String id = adv.getID() == null ? "null" : adv.getID().toString();
        if (KADEMLIA_BOOTSTRAP_ADV_ID_STR.equals(id)) {
          CommAddress bootstrapAddress = new CommAddress(PeerID.create(URI
              .create(((PipeAdv) adv).getDescription())));

          if (!bootstrapAddress.toString().equals(peerAddress_.toString()) &&
              (bootstrapCount_ < MAX_BOOTSTRAP_COUNT)) {
            bootstrapCount_++;

            try {
              logger_.info("Kademlia is trying to bootstrap to " +
                  bootstrapAddress.toString());

              kademlia_.connect(bootstrapAddress);
            } catch (IOException e) {
              logger_.error(e);
            }
            logger_.info("Kademlia bootstraped with " +
                bootstrapAddress.toString());
            logger_.info("Kademlia contents:" + kademlia_.toString());
          }
        }

      }
    } else {
      logger_.debug("discoveryEvent empty...");
    }

  }

  @Override
  public String toString() {
    return kademlia_.toString();
  }
}
