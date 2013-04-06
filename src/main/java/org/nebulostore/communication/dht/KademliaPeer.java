package org.nebulostore.communication.dht;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import com.google.inject.Inject;

import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.appcore.EndModuleMessage;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.messages.dht.DelDHTMessage;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.OutDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Kademlia DHT using TP2P library.
 *
 * This library connects to TP2P inititated by Bootstrap (using master
 * Peer).
 * @author Grzegorz Milka
 */
public class KademliaPeer extends Module {
  private static Logger logger_ = Logger.getLogger(KademliaPeer.class);
  private static final String CONFIG_PREFIX = "communication.dht.kademlia-peer.";
  private net.tomp2p.p2p.Peer peer_;
  private net.tomp2p.p2p.Peer masterPeer_;

  private XMLConfiguration config_;
  private MessageVisitor<Void> msgVisitor_;

  @Inject
  public void setConfig(XMLConfiguration config) {
    config_ = config;
  }

  /**
   * @param inQueue
   * @param outQueue
   */
  public KademliaPeer(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue,
      Peer masterPeer) {
    super(inQueue, outQueue);
    masterPeer_ = masterPeer;
    msgVisitor_ = new KademliaPeerMessageVisitor();
  }

  @Override
  public void run() {
    try {
      initPeer();
    } catch (IOException e) {
      /* TODO(grzegorzmilka) How should I notify anyone init has failed? */
      logger_.error("IOException when initializing KademliaPeer. " + e);
      return;
    }
    super.run();
  }

  private void initPeer() throws IOException {
    checkNotNull(config_);
    /* Using random here, it should be sufficient against collision.
     * Findbugs generates a warning, but I ignore it, since we use the random
     * number generator only once */
    peer_ = new PeerMaker(new Number160((new Random()).nextLong())).
      setMasterPeer(masterPeer_).makeAndListen();

    FutureDiscover futDiscovery = peer_.discover().setPeerAddress(
        masterPeer_.getPeerAddress()).start();
    futDiscovery.awaitUninterruptibly();
    if (!futDiscovery.isSuccess()) {
      logger_.info("Discovery has failed.");
      return;
    }
    FutureBootstrap futBootstrap = peer_.bootstrap().setPeerAddress(
        masterPeer_.getPeerAddress()).start();
    futBootstrap.awaitUninterruptibly();
    if (!futBootstrap.isSuccess()) {
      logger_.info("Bootstrap has failed.");
      /* TODO(grzegorzmilka) Add Nebulostore Exception here */
      return;
    }
    logger_.info("Fully initialized.");
  }

  private void shutdown() {
    logger_.info("Ending kademlia peer");
    endModule();
  }

  private void get(GetDHTMessage getMsg) {
    logger_.trace("Entering GetDHTMessage");
    KeyDHT key = getMsg.getKey();

    Data data = getData(key);
    logger_.trace("Got following data: " + data + ", for key: " + key);

    OutDHTMessage outMessage;
    try {
      if (data != null && (data.getObject() instanceof ValueDHT)) {
        ValueDHT value = (ValueDHT) data.getObject();
        outMessage = new ValueDHTMessage(getMsg, key, value);
      } else {
        logger_.debug("Unable to read from dht. Sending ErrorDHTMessage.");
        outMessage = new ErrorDHTMessage(getMsg, new NebuloException(
              "Unable to read from tp2p database."));
      }
    } catch (IOException e) {
      logger_.warn("IOException at data.getObject. Data: " + data +
          ", exception: " + e);
      outMessage = new ErrorDHTMessage(getMsg, new NebuloException(
            "IOException when getting dataObject"));
    } catch (ClassNotFoundException e) {
      logger_.warn("ClassNotFoundException at data.getObject. Data: " + data +
          ", exception: " + e);
      outMessage = new ErrorDHTMessage(getMsg, new NebuloException(
            "ClassNotFoundException when getting dataObject"));
    }

    outQueue_.add(outMessage);
    logger_.debug("GetDHTMessage processing finished");
  }

  /**
   * Returns not null iff successful.
   * @author Grzegorz Milka
   */
  private Data getData(KeyDHT key) {
    Number160 nr = KeyDHT.combine(KeyDHT.NONADDRESSING_KEY, key.getNumber160());

    FutureDHT futureDHT = peer_.get(nr).start();
    futureDHT.awaitUninterruptibly();
    return futureDHT.getData();
  }

  private void put(PutDHTMessage putMsg) {
    logger_.info("Entering PutDHTMessage(" + putMsg.getId() + ") with " +
        putMsg.getKey() + " : " + putMsg.getValue());

    KeyDHT key = putMsg.getKey();
    ValueDHT valueDHT = putMsg.getValue();

    /* Get new old value and merge */
    Data data = getData(key);
    Object dataObject = null;
    if (data == null) {
      logger_.trace("Key: " + key + " not present in DHT");
      /* Do nothing */
    }  else {
      try {
        dataObject = data.getObject();
      } catch (IOException e) {
        logger_.warn("IOException at data.getObject. Data: " + data +
            ", exception: " + e);
        outQueue_.add(new ErrorDHTMessage(putMsg, new NebuloException(
                "IOException when getting dataObject")));
        return;
      } catch (ClassNotFoundException e) {
        logger_.warn("ClassNotFoundException at data.getObject. Data: " + data +
            ", exception: " + e);
        outQueue_.add(new ErrorDHTMessage(putMsg, new NebuloException(
                "ClassNotFoundException when getting dataObject")));
        return;
      }
      if (!(dataObject instanceof ValueDHT)) {
        logger_.warn("Key: " + key + " does not contain ValueDHT. Aborting put.");
        outQueue_.add(new ErrorDHTMessage(putMsg, new NebuloException(
                "Wrong object present under key: " + key)));
        return;
      } else {
        ValueDHT oldValue = (ValueDHT) dataObject;
        logger_.trace("Key: " + key + ", present with current value: " + oldValue);
        valueDHT = new ValueDHT(valueDHT.getValue().merge(oldValue.getValue()));
      }
    }

    boolean isSuccess = putData(key, valueDHT);
    if (isSuccess) {
      logger_.info("PutDHTMessage processing finished");
      outQueue_.add(new OkDHTMessage(putMsg));
    } else {
      logger_.warn("PutDHTMessage encountered error during put operation");
      outQueue_.add(new ErrorDHTMessage(putMsg, new NebuloException(
              "Couldn't put object to DHT under key: " + key)));
    }
  }


  /**
   * Puts given key, value pair to DHT under the layer keys.
   *
   * Return true iff successful.
   * @author Grzegorz Milka
   */
  private boolean putData(KeyDHT key, ValueDHT value) {
    Number160 nr = KeyDHT.combine(KeyDHT.NONADDRESSING_KEY, key.getNumber160());

    FutureDHT futureDHT;
    try {
      futureDHT = peer_.put(nr).setData(new Data(value)).start();
      futureDHT.awaitUninterruptibly();
    } catch (IOException e) {
      logger_.warn("Caught IOException: " + e + " at putData for key: " + key);
      return false;
    }

    return futureDHT.isSuccess();
  }

  private void remove(DelDHTMessage delMsg) {
    logger_.trace("Entering remove");
    KeyDHT key = delMsg.getKey();

    Number160 nr = KeyDHT.combine(KeyDHT.NONADDRESSING_KEY, key.getNumber160());

    FutureDHT futureDHT = peer_.remove(nr).start();
    futureDHT.awaitUninterruptibly();
    if (futureDHT.isSuccess()) {
      logger_.info("DelDHTMessage processing finished");
      outQueue_.add(new OkDHTMessage(delMsg));
    } else {
      logger_.warn("DelDHTMessage encountered error during put operation");
      outQueue_.add(new ErrorDHTMessage(delMsg, new NebuloException(
              "Couldn't remove object from DHT under key: " + key)));
    }
    logger_.debug("DelDHTMessage processing finished");
  }

  @Override
  protected void processMessage(Message msg) throws NebuloException {
    logger_.debug("Processing message: " + msg);
    msg.accept(msgVisitor_);
  }

  /**
   * Message Visitor for KademliaPeer.
   *
   * @author Grzegorz Milka
   */
  private final class KademliaPeerMessageVisitor extends MessageVisitor<Void> {
    @Override
    public Void visit(EndModuleMessage msg) {
      shutdown();
      return null;
    }

    @Override
    public Void visit(PutDHTMessage msg) {
      put(msg);
      return null;
    }

    @Override
    public Void visit(GetDHTMessage msg) {
      get(msg);
      return null;
    }

    @Override
    public Void visit(DelDHTMessage msg) {
      remove(msg);
      return null;
    }
  }
}
