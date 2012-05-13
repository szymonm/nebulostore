package org.nebulostore.communication.jxta;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.security.cert.CertificateException;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.exception.PeerGroupException;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.DiscoveryMessage;
import org.nebulostore.communication.streambinding.IStreamBindingDriver;
import org.nebulostore.communication.streambinding.StreamBindingService;

/**
 * @author Marcin Walas
 */
public class JXTAPeer extends Module implements DiscoveryListener {

  private String bootstrapUrl_ = "http://students.mimuw.edu.pl/~mw262460/bootstrap2.rdv";
  private String fallbackBootstrapUrl_ = "http://students.mimuw.edu.pl/~mw262460/bootstrap2.rdv";

  private static final int PEER_ROLES = NetworkConfigurator.RDV_SERVER |
      NetworkConfigurator.RDV_CLIENT | NetworkConfigurator.RELAY_CLIENT |
      NetworkConfigurator.RELAY_SERVER | NetworkConfigurator.EDGE_NODE;

  private static final String CONFIGURATION_PATH = "resources/conf/communication/JxtaPeer.xml";

  private static String peerName_ = "somepeername";

  private transient MessengerService messengerService_;
  private transient MessageReceiver messageReceiver_;

  private transient SocketServer socketServer_;
  private transient PeerDiscoveryService peerDiscoveryService_;

  private transient NetworkManager networkManager_;
  private transient DiscoveryService discoveryService_;

  private transient NetworkConfigurator networkConfigurator_;

  private final BlockingQueue<Message> messengerServiceInQueue_;

  private static Logger logger_ = Logger.getLogger(JXTAPeer.class);

  private final List<CommAddress> knownPeers_;

  private List<String> seedingURIs_;
  public static boolean startFeeding_ = false;

  public JXTAPeer(BlockingQueue<Message> jxtaPeerIn,
      BlockingQueue<Message> jxtaPeerOut) {
    super(jxtaPeerIn, jxtaPeerOut);

    logger_.info("Ctor invoked. Configuring Network manager.");
    try {
      initNetworkManager();
    } catch (NebuloException e) {
      logger_.error(e);
      System.exit(-1);
    }
    logger_.info("Network manager configured with PeerID: " +
        networkManager_.getPeerID().toString());

    logger_.info("Starting JXTA Network manager... ");
    try {
      startNetworkManager();
    } catch (NebuloException e) {
      logger_.error(e);
      System.exit(-1);
    }
    logger_.info("Network manager started successfuly");

    // discovery init
    discoveryService_.addDiscoveryListener(this);
    knownPeers_ = new LinkedList<CommAddress>();

    // socket server
    logger_.info("Starting socket server");
    socketServer_ = new SocketServer(networkManager_.getNetPeerGroup());
    new Thread(socketServer_, "Nebulostore.Communication.SocketServer").start();
    logger_.info("Socket server started");

    // messengers init
    messengerServiceInQueue_ = new LinkedBlockingQueue<Message>();

    messengerService_ = new MessengerService(messengerServiceInQueue_,
        outQueue_, networkManager_.getNetPeerGroup());
    (new Thread(messengerService_, "Nebulostore.Communication.MessengerService"))
    .start();

    try {
      messageReceiver_ = new MessageReceiver(networkManager_.getNetPeerGroup()
          .getPipeService()
          .createInputPipe(MessengerService.getPipeAdvertisement()), outQueue_);

      (new Thread(messageReceiver_, "Nebulostore.Communication.MeesageReceiver"))
      .start();
    } catch (IOException e) {
      logger_.error(e);
      System.exit(-1);
    }

    peerDiscoveryService_ = new PeerDiscoveryService(discoveryService_);
    (new Thread(peerDiscoveryService_,
        "Nebulostore.Communication.PeerDiscoveryService")).start();

    knownPeers_.add(getPeerAddress());
    (new Timer()).schedule(new GossipPeers(), 2000, 1000);

    logger_.info("fully initialised");
  }

  private void readConfig() {
    XMLConfiguration config = null;
    try {
      config = new XMLConfiguration(CONFIGURATION_PATH);
    } catch (ConfigurationException cex) {
      logger_.error("Configuration read error in: " + CONFIGURATION_PATH);
    }

    bootstrapUrl_ = config.getString("jxta.boostrap-url", bootstrapUrl_);
    fallbackBootstrapUrl_ = config.getString("jxta.fallback-boostrap-url",
        fallbackBootstrapUrl_);

  }

  /**
   * Initializes and saves to hard drive configuration of JXTA, NetworkManager
   * providing peer unique identity. All errors here are treated as fatal and
   * result in application unclean shutdown.
   * 
   * @throws NebuloException
   */
  private void initNetworkManager() throws NebuloException {

    try {
      networkManager_ = new NetworkManager(NetworkManager.ConfigMode.EDGE,
          peerName_);
    } catch (IOException e) {
      logger_.fatal(e);
      System.exit(-1);
    }

    networkManager_.setConfigPersistent(true);
    logger_.info("PeerID: " + networkManager_.getPeerID().toString());

    // Retrieving the Network Configurator
    logger_.info("Retrieving the Network Configurator");

    try {
      networkConfigurator_ = networkManager_.getConfigurator();
    } catch (IOException e) {
      logger_.error(e);
    }

    logger_.info("Network Configurator retrieved");
    // Does a local peer configuration exist?
    if (networkConfigurator_.exists()) {
      logger_.info("Local configuration found");
      // We load it
      File localConfig = new File(networkConfigurator_.getHome(),
          "PlatformConfig");
      try {
        logger_.info("Loading found configuration");
        networkConfigurator_.load(localConfig.toURI());
        logger_.info("Configuration loaded");
      } catch (IOException e) {
        logger_.fatal(e);
        System.exit(-1);
      } catch (CertificateException e) {
        logger_.fatal(e);
        System.exit(-1);
      }

    } else {
      logger_.info("No local configuration found");
      logger_.info("Creating new one.");

      seedingURIs_ = new LinkedList<String>();
      seedingURIs_.add(bootstrapUrl_);
      seedingURIs_.add(fallbackBootstrapUrl_);

      networkConfigurator_.setName(peerName_);
      networkConfigurator_.setTcpPort(9766);

      networkConfigurator_.setMode(PEER_ROLES);
      networkConfigurator_.setRendezvousSeedingURIs(seedingURIs_);
      networkConfigurator_
      .setRelaySeedingURIs(new HashSet<String>(seedingURIs_));

      try {
        logger_.info("Saving new configuration");
        networkConfigurator_.save();
        logger_.info("New configuration saved successfully");
      } catch (IOException e) {
        logger_.fatal(e);
        System.exit(-1);
      }
    }

  }

  private void startNetworkManager() throws NebuloException {
    try {
      networkManager_.startNetwork();
    } catch (IOException e) {
      logger_.error(e);
      throw new NebuloException(e);
    } catch (PeerGroupException e) {
      logger_.error(e);
      throw new NebuloException(e);
    }

    PeerGroup netPeerGroup = networkManager_.getNetPeerGroup();
    // get the discovery service
    discoveryService_ = netPeerGroup.getDiscoveryService();
  }

  @Override
  protected void processMessage(Message msg) {

    if (msg instanceof DiscoveryMessage) {
      logger_.debug("DiscoveryMessage message : emitting events");
      newPeersFound(((DiscoveryMessage) msg).getKnownPeers());
      return;
    }

    if (msg instanceof CommMessage) {
      logger_.debug("message of class: " + msg.getClass() +
          " forwarded to MessengerService");
      ((CommMessage) msg).setSourceAddress(getPeerAddress());
      messengerServiceInQueue_.add(msg);
      return;
    }
  }

  public void setStreamBindingService(StreamBindingService service) {
    socketServer_.setStreamBindingService(service);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.jxta.discovery.DiscoveryListener#discoveryEvent(net.jxta.discovery.
   * DiscoveryEvent)
   */
  @Override
  public void discoveryEvent(DiscoveryEvent ev) {
    logger_.info("DiscoveryEvent received with address: " + ev.getSource());

    try {
      // TODO: move this URI mod to communication.utils
      CommAddress discoveredAddress = new CommAddress(PeerID.create(new URI(
          "urn:" + ("" + ev.getSource()).replace("//", ""))));
      List<CommAddress> tmp = new LinkedList<CommAddress>();
      tmp.add(discoveredAddress);
      newPeersFound(tmp);
    } catch (URISyntaxException e) {
      logger_.error("URISyntaxException", e);
    }

  }

  private class GossipPeers extends TimerTask {

    @Override
    public void run() {
      logger_.info("Gossiping known peers...");
      synchronized (knownPeers_) {
        for (CommAddress address : knownPeers_) {
          if (!address.toString().equals(getPeerAddress().toString())) {
            messengerServiceInQueue_.add(new DiscoveryMessage(getPeerAddress(),
                address, new LinkedList<CommAddress>(knownPeers_)));
          }
        }
      }
    }

  }

  private void newPeersFound(List<CommAddress> addresses) {
    synchronized (knownPeers_) {
      for (CommAddress address : addresses) {
        if (startFeeding_ && !knownPeers_.contains(address)) {
          logger_.info("New peer found with address: " + address);
          knownPeers_.add(address);
          if (!address.toString().equals(getPeerAddress().toString())) {
            messengerServiceInQueue_.add(new DiscoveryMessage(getPeerAddress(),
                address, new ArrayList<CommAddress>(knownPeers_)));
          }
          outQueue_.add(new CommPeerFoundMessage(address, getPeerAddress()));
        }
      }
    }
  }

  public CommAddress getPeerAddress() {
    return new CommAddress(networkManager_.getPeerID());
  }

  public PeerDiscoveryService getPeerDiscoveryService() {
    return peerDiscoveryService_;
  }

  public IStreamBindingDriver getStreamDriver() {
    return socketServer_;
  }
}
