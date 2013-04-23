package org.nebulostore.communication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.LogManager;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.appcore.EndModuleMessage;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.bootstrap.BootstrapServer;
import org.nebulostore.communication.bootstrap.BootstrapService;
import org.nebulostore.communication.dht.BdbPeer;
import org.nebulostore.communication.dht.KademliaPeer;
import org.nebulostore.communication.dht.messages.BdbMessageWrapper;
import org.nebulostore.communication.dht.messages.DHTMessage;
import org.nebulostore.communication.dht.messages.HolderAdvertisementMessage;
import org.nebulostore.communication.dht.messages.InDHTMessage;
import org.nebulostore.communication.dht.messages.OutDHTMessage;
import org.nebulostore.communication.gossip.GossipService;
import org.nebulostore.communication.gossip.GossipServiceFactory;
import org.nebulostore.communication.gossip.messages.PeerGossipMessage;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;
import org.nebulostore.communication.messages.ReconfigureDHTAckMessage;
import org.nebulostore.communication.messages.ReconfigureDHTMessage;
import org.nebulostore.communication.socket.ListenerService;
import org.nebulostore.communication.socket.MessengerService;
import org.nebulostore.communication.socket.MessengerServiceFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Main module for communication with outside world.
 *
 * So far it only handles BDB.
 * @author Marcin Walas
 * @author Grzegorz Milka
 */
public final class CommunicationPeer extends Module {
  private static Logger logger_ = Logger.getLogger(CommunicationPeer.class);
  private static final String CONFIG_PREFIX = "communication.";
  private XMLConfiguration config_;
  private MessageVisitor<Void> msgVisitor_;
  private CommAddress commAddress_;

  /**
   * Module for handling bootstraping peer to network.
   *
   * It can be either server or client, depending on configuration
   */
  private BootstrapService bootstrapService_;
  private boolean isServer_;

  /**
   * DHT module available to higher layers.
   *
   * Note that it was implemented by Marcin and I(grzegorzmilka) left it mostly
   * as is. Only BDB works.
   */
  private Module dhtPeer_;
  private BlockingQueue<Message> dhtPeerInQueue_;
  private Thread dhtPeerThread_;

  /**
   * Main module for listening for messages.
   *
   * It works listens for incoming TCP connections from messengerService
   */
  private ListenerService listenerService_;
  private Thread listenerThread_;

  /**
   * Main module for sending messages across the network.
   */
  private MessengerService messengerService_;
  private MessengerServiceFactory messengerServiceFactory_;
  private BlockingQueue<Message> messengerServiceInQueue_;
  private Thread messengerThread_;

  /**
   * Module handling peer gossiping.
   *
   * Once every x seconds gossiper gossips to get updated view on the network.
   */
  private BlockingQueue<Message> gossipServiceInQueue_;
  private GossipServiceFactory gossipServiceFactory_;
  private GossipService gossipService_;
  private Thread gossipThread_;

  // Is the server shutting down.
  private AtomicBoolean isEnding_ = new AtomicBoolean(false);

  private int commCliPort_;

  @Inject
  public CommunicationPeer(
          @Named("CommunicationPeerInQueue") BlockingQueue<Message> inQueue,
          @Named("CommunicationPeerOutQueue") BlockingQueue<Message> outQueue,
          XMLConfiguration config,
          CommAddress commAddress,
          BootstrapService bootstrapService,
          @Named("IsServer") boolean isServer,
          GossipServiceFactory gossipServiceFactory,
          ListenerService listenerService,
          MessengerServiceFactory messengerServiceFactory) {
    super(inQueue, outQueue);
    config_ = config;
    commAddress_ = commAddress;
    bootstrapService_ = bootstrapService;
    isServer_ = isServer;
    gossipServiceInQueue_ = new LinkedBlockingQueue<Message>();
    gossipServiceFactory_ = gossipServiceFactory;
    listenerService_ = listenerService;
    messengerServiceInQueue_ = new LinkedBlockingQueue<Message>();
    messengerServiceFactory_ = messengerServiceFactory;
    msgVisitor_ = new CommPeerMsgVisitor();

    dhtPeerInQueue_ = new LinkedBlockingQueue<Message>();
  }

  @Override
  protected void initModule() {
    try {
      initPeer();
    } catch (NebuloException e) {
      throw new RuntimeException("Unable to initialize network!", e);
    }
  }

  private void initPeer() throws NebuloException {
    logger_.debug("Starting CommunicationPeer with CommAddress = " + commAddress_);
    checkNotNull(config_);

    String clingConfPath = config_.getString(CONFIG_PREFIX + "cling-config");
    if (clingConfPath != null) {
      readClingConfig(clingConfPath);
    }

    /* Initialize bootstrap service */
    if (isServer_) {
      try {
        bootstrapService_.startUpService();
      } catch (IOException e) {
        logger_.error("IOException: " + e +
            " caught when starting up BootstrapServer.");
        throw new NebuloException("IOException at bootstrap", e);
      }
      Thread bootstrap = new Thread((BootstrapServer) bootstrapService_,
          "Nebulostore.Communication.Bootstrap");
      bootstrap.setDaemon(true);
      bootstrap.start();
      logger_.info("Created BootstrapServer.");
    }

    /* Add current resolver to context */
    messengerService_ = messengerServiceFactory_.newMessengerService(
            messengerServiceInQueue_,
            inQueue_,
            bootstrapService_.getResolver());

    gossipService_ = gossipServiceFactory_.newGossipService(
        gossipServiceInQueue_,
        inQueue_,
        bootstrapService_.getBootstrapCommAddress());

    /* Start submodule's threads */
    listenerThread_ = new Thread(
        listenerService_, "Nebulostore.Communication.ListenerService");
    listenerThread_.setDaemon(true);
    listenerThread_.start();
    gossipThread_ =
      new Thread(gossipService_, "Nebulostore.Communication.GossipService");
    gossipThread_.setDaemon(true);
    gossipThread_.start();
    messengerThread_ = new Thread(
        messengerService_, "Nebulostore.Communication.MessengerService");
    messengerThread_.setDaemon(true);
    messengerThread_.start();

    logger_.info("Created and started auxiliary services.");

    String dhtProvider = config_.getString(CONFIG_PREFIX + "dht.provider", "bdb");
    if (!dhtProvider.equals("none")) {
      reconfigureDHT(dhtProvider, null);
    } else {
      dhtPeer_ = null;
    }
  }

  private void readClingConfig(String clingConfPath) {
    /* Turn off cling's logging by turning off JUL - java.util.logging*/
    FileInputStream fileIS = null;
    try {
      LogManager logManager = LogManager.getLogManager();
      fileIS = new FileInputStream(clingConfPath);
      logManager.readConfiguration(fileIS);
    } catch (IOException e) {
      logger_.warn("IOException: " + e + " was thrown when trying to read " +
          "cling configuration");
    } finally {
      if (fileIS != null) {
        try {
          fileIS.close();
        } catch (IOException e) {
          logger_.warn("IOException: " + e +
              " was thrown when trying to close fileIS");
        } finally {
          fileIS = null;
        }
      }
    }
  }

  /**
   * Kills this peer with its submodules.
   *
   * During the shutting down any messages sent to this peer will generate warn
   * log message. Remember to call interrupt when after call to endModule.
   *
   * @author Grzegorz Milka
   */
  private void shutdown() {
    logger_.info("Starting shutdown procedure of CommunicationPeer.");
    isEnding_.set(true);
    /* Start cancelling modules */

    /* End dhtPeer if any */
    try {
      if (dhtPeer_ != null) {
        dhtPeerInQueue_.add(new EndModuleMessage());
        dhtPeerThread_.join();
        logger_.info("DHT thread ended.");
      }
    } catch (InterruptedException e) {
      logger_.info("Caught InterruptedException when joining dht thread.");
      /* set interrupted flag for this thread */
      Thread.currentThread().interrupt();
    }

    /* End messengerService */
    messengerServiceInQueue_.add(new EndModuleMessage());
    try {
      messengerThread_.join();
      logger_.info("Messenger thread ended.");
    } catch (InterruptedException e) {
      logger_.info("Caught InterruptedException when joining messenger thread.");
      /* set interrupted flag for this thread */
      Thread.currentThread().interrupt();
    }

    /* End listenerService */
    listenerService_.shutdown();
    try {
      listenerThread_.join();
      logger_.info("Listener thread ended.");
    } catch (InterruptedException e) {
      logger_.info("Caught InterruptedException when joining listener.");
      /* set interrupted flag for this thread */
      Thread.currentThread().interrupt();
    }

    /* End gossipService */
    gossipServiceInQueue_.add(new EndModuleMessage());
    try {
      gossipThread_.join();
      logger_.info("Gossip thread ended.");
    } catch (InterruptedException e) {
      logger_.warn("Caught InterruptedException when joining gossiper.");
      /* set interrupted flag for this thread */
      Thread.currentThread().interrupt();
    }

    bootstrapService_.shutdownService();
    logger_.info("BootstrapService shutdown.");
    endModule();
  }

  @Override
  protected void processMessage(Message msg) throws NebuloException {
    if (isEnding_.get()) {
      logger_.warn("Can not process message, because commPeer is shutting down.");
      return;
    }
    logger_.debug("Processing message: " + msg);
    msg.accept(msgVisitor_);
  }

  /**
   * Message Visitor for CommunicationPeer.
   *
   * @author Grzegorz Milka
   */
  private final class CommPeerMsgVisitor extends MessageVisitor<Void> {
    @Override
    public Void visit(EndModuleMessage msg) {
      logger_.info("Received EndModule message");
      isEnding_.set(true);
      shutdown();

      return null;
    }

    @Override
    public Void visit(ErrorCommMessage msg) {
      logger_.info("Error comm message. Returning it to Dispatcher");
      gossipServiceInQueue_.add(msg);
      outQueue_.add(msg);

      return null;
    }

    @Override
    public Void visit(ReconfigureDHTMessage msg) {
      try {
        logger_.info("Got reconfigure request with jobId: " + msg.getId());
        reconfigureDHT(((ReconfigureDHTMessage) msg).getProvider(),
            (ReconfigureDHTMessage) msg);
      } catch (NebuloException e) {
        logger_.warn(e);
      }

      return null;
    }

    public Void visit(HolderAdvertisementMessage msg) {
      dhtPeerInQueue_.add(msg);
      return null;
    }

    @Override
    public Void visit(CommPeerFoundMessage msg) {
      logger_.debug("CommPeerFound message forwarded to Dispatcher");
      outQueue_.add(msg);

      return null;
    }

    @Override
    public Void visit(DHTMessage msg) {
      if (msg instanceof InDHTMessage) {
        logger_.debug("InDHTMessage forwarded to DHT" + msg.getClass().getSimpleName());
        dhtPeerInQueue_.add(msg);
      } else if (msg instanceof OutDHTMessage) {
        logger_.debug("OutDHTMessage forwarded to Dispatcher" + msg.getClass().getSimpleName());
        outQueue_.add(msg);
      } else {
        logger_.warn("Unrecognized DHTMessage: " + msg);
      }

      return null;
    }

    @Override
    public Void visit(BdbMessageWrapper msg) {
      logger_.debug("BDB DHT message received");
      BdbMessageWrapper casted = (BdbMessageWrapper) msg;
      if (casted.getWrapped() instanceof InDHTMessage) {
        logger_.debug("BDB DHT message forwarded to DHT");
        dhtPeerInQueue_.add(casted);
      } else if (casted.getWrapped() instanceof OutDHTMessage) {
        logger_.debug("BDB DHT message forwarded to Dispatcher");
        outQueue_.add(casted.getWrapped());
      } else {
        logger_.warn("Unrecognized BdbMessageWrapper: " + msg);
      }
      return null;

    }

    @Override
    public Void visit(PeerGossipMessage msg) {
      if (((CommMessage) msg).getDestinationAddress().equals(
            bootstrapService_.getResolver().getMyCommAddress())) {
        gossipServiceInQueue_.add(msg);
      } else {
        messengerServiceInQueue_.add(msg);
      }
      return null;
    }

    @Override
    public Void visit(CommMessage msg) {
      if (((CommMessage) msg).getSourceAddress() == null) {
        ((CommMessage) msg).setSourceAddress(commAddress_);
      }

      if (((CommMessage) msg).getDestinationAddress() == null) {
        logger_.warn("Null destination address set for " + msg + ". Dropping the message.");
      } else if (((CommMessage) msg).getDestinationAddress().equals(
            bootstrapService_.getResolver().getMyCommAddress())) {
        logger_.debug("message forwarded to Dispatcher");
        outQueue_.add(msg);
      } else {
        logger_.debug("message forwarded to MessengerService");
        messengerServiceInQueue_.add(msg);
      }
      return null;
    }
  }

  /**
   * Starts up and configures DHTPeer.
   *
   * @author Marcin Walas
   * @author Grzegorz Milka
   */
  private void reconfigureDHT(String dhtProvider,
      ReconfigureDHTMessage reconfigureRequest) throws NebuloException {

    if (dhtProvider.equals("bdb") && (dhtPeer_ instanceof BdbPeer)) {
      if (reconfigureRequest != null && ((BdbPeer) dhtPeer_).getHolderAddress() != null) {
        outQueue_.add(new ReconfigureDHTAckMessage(reconfigureRequest));
      }
    } else {
      if (dhtPeerThread_ != null) {
        dhtPeerInQueue_.add(new EndModuleMessage());
        try {
          dhtPeerThread_.join();
        } catch (InterruptedException e) {
          logger_.info("Caught InterruptedException when joining dhtPeer " +
              "Thread.");
          /* set interrupted flag for this thread */
          Thread.currentThread().interrupt();
        }
      }

      if (dhtProvider.equals("bdb")) {
        BdbPeer bdbPeer = new BdbPeer(dhtPeerInQueue_, outQueue_,
            messengerServiceInQueue_, reconfigureRequest);
        bdbPeer.setConfig(config_);
        bdbPeer.setCommAddress(commAddress_);
        dhtPeer_ = bdbPeer;
      } else if (dhtProvider.equals("kademlia")) {
        KademliaPeer kademliaPeer = new KademliaPeer(dhtPeerInQueue_, outQueue_,
            bootstrapService_.getTP2PPeer());
        kademliaPeer.setConfig(config_);
        dhtPeer_ = kademliaPeer;
      } else {
        throw new NebuloException("Unsupported DHT Provider in configuration");
      }
      dhtPeerThread_ = new Thread(dhtPeer_, "Nebulostore.Communication.DHT");
      dhtPeerThread_.setDaemon(true);
      dhtPeerThread_.start();
      logger_.info("(Re)started DHT Thread");
    }
  }
}
