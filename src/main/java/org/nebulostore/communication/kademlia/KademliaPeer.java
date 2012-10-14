package org.nebulostore.communication.kademlia;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;


import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.ValueDHT;
import org.nebulostore.communication.dht.exceptions.ValueNotFound;
import org.nebulostore.communication.messages.ReconfigureDHTAckMessage;
import org.nebulostore.communication.messages.ReconfigureDHTMessage;
import org.nebulostore.communication.messages.dht.DHTMessage;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.communication.messages.kademlia.KademliaMessage;
import org.nebulostore.networkmonitor.NetworkContext;
import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Kademlia;

/**
 * Internal Module wrapper for DHT based on Kademlia functionlity.
 *
 * @author Marcin Walas
 */
public class KademliaPeer extends Module {

  private static Logger logger_ = Logger.getLogger(KademliaPeer.class);
  /*"urn:jxta:" + "uuid-59616261646162614E504720503250338944BCED387C4A2BBD8E9411B78C28FF04"*/
  private static final String KADEMLIA_BOOTSTRAP_ADV_ID_STR = "";

  private static final int MAX_BOOTSTRAP_COUNT = 3;

  private final CommAddress peerAddress_;

  private final BlockingQueue<Message> kademliaQueue_;
  private final BlockingQueue<Message> workerInQueue_;

  private Kademlia kademlia_;
  private static Kademlia kademliaStatic_;

  private int bootstrapCount_;

  private final List<MessageWorker> messageWorkers_;

  private final ReconfigureDHTMessage reconfigureRequest_;
  private final Set<CommAddress> alreadyBootstraped_;

  private final Timer bootstrapTimer_;

  public KademliaPeer(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue,
      CommAddress peerAddress,
      BlockingQueue<Message> jxtaInQueue,
      ReconfigureDHTMessage reconfigureRequest) throws NebuloException {
    super(inQueue, outQueue);
    throw new UnsupportedOperationException("Kademlia unsupported for now");
  }

  /**
   * @author Marcin Walas
   */
  class BootstrapTask extends TimerTask {
    @Override
    public void run() {
      /* TODO: Register in NetworkContext also...!!! */
      for (CommAddress client : NetworkContext.getInstance().getKnownPeers()) {
        bootstrapWithAddress(client);
      }
    }

  }

  @Override
  public void endModule() {
    throw new UnsupportedOperationException("Kademlia unsupported for now");
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
      if (val == null) {
        throw new ValueNotFound("Not found in DHT: " + keyId);
      }
      outQueue_.add(new ValueDHTMessage(msg, msg.getKey(), val));
      logger_.info("get of key: " + keyId + " finished");
    } catch (Exception exception) {
      logger_.error(exception);
      outQueue_.add(new ErrorDHTMessage(msg, new NebuloException(exception)));
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
      outQueue_.add(new ErrorDHTMessage(msg, new NebuloException(e)));
    }
  }

  /**
   * Internal class used to handle external put/get requests from other modules.
   * Introduced because of the fact that queue for incoming messages in
   * KademliaPeer is collecting operations from modules as well as network
   * reponses from remote hosts.
   *
   * @author Marcin Walas
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

  public Object getKademliaAdvertisement() {
    throw new UnsupportedOperationException("Kademlia unsupported for now");
  }

  private void bootstrapWithAddress(CommAddress bootstrapAddress) {
    bootstrapCount_++;
    if (alreadyBootstraped_.contains(bootstrapAddress)) {
      return;
    }

    try {
      logger_.info("Kademlia is trying to bootstrap with " +
          bootstrapAddress.toString());
      kademlia_.connect(bootstrapAddress);

      logger_.info("Kademlia bootstraped with " + bootstrapAddress.toString());
      logger_.info("Kademlia contents:" + kademlia_.toString());
      alreadyBootstraped_.add(bootstrapAddress);
      if (bootstrapCount_ == 1 && reconfigureRequest_ != null) {
        outQueue_.add(new ReconfigureDHTAckMessage(reconfigureRequest_));
      }

    } catch (IOException e) {
      logger_.error(e);
    }

  }

  public static String getKademliaContents() {
    if (kademliaStatic_ != null) {
      return kademliaStatic_.toString();
    } else {
      return "KADEMLIA NOT INITIALIZED";
    }

  }

  @Override
  public String toString() {
    return kademlia_.toString();
  }

}
