package org.nebulostore.communication.jxta;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import net.jxta.protocol.DiscoveryResponseMsg;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;

/**
 * @author Marcin Walas
 */
public class JXTAPeer extends Module implements DiscoveryListener {

  private static String peerName_ = "mypeername";

  private transient MessengerService messengerService_;

  private transient SocketServer socketServer_;
  private final transient PeerDiscoveryService peerDiscoveryService_;

  private transient NetworkManager networkManager_;
  private transient DiscoveryService discoveryService_;

  private transient NetworkConfigurator networkConfigurator_;

  private final BlockingQueue<Message> messengerServiceInQueue_;

  private static Logger logger_ = Logger.getLogger(JXTAPeer.class);

  private final List<String> knownPeers_;

  private List<String> seedingURIs;

  public JXTAPeer(BlockingQueue<Message> jxtaPeerIn,
      BlockingQueue<Message> jxtaPeerOut) {
    super(jxtaPeerIn, jxtaPeerOut);

    try {
      networkManager_ = new NetworkManager(NetworkManager.ConfigMode.EDGE,
          peerName_);
    } catch (IOException e) {
      e.printStackTrace();
      // TODO MBW: Better handle this situation
      System.exit(-1);
    }

    networkManager_.setConfigPersistent(true);
    logger_.info("PeerID: " + networkManager_.getPeerID().toString());

    // Retrieving the Network Configurator
    logger_.info("Retrieving the Network Configurator");
    try {
      networkConfigurator_ = networkManager_.getConfigurator();
    } catch (IOException e) {
      e.printStackTrace();
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
      } catch (IOException ex) {
        ex.printStackTrace();
        System.exit(-1);
      } catch (CertificateException ex) {
        // An issue with the existing peer certificate has been encountered
        ex.printStackTrace();
        System.exit(-1);
      }
    } else {
      logger_.info("No local configuration found");

      seedingURIs = new LinkedList<String>();
      seedingURIs.add("http://students.mimuw.edu.pl/~mw262460/bootstrap.rdv");

      networkConfigurator_.setName(peerName_);
      networkConfigurator_.setPrincipal("asdf");
      networkConfigurator_.setPassword("asfd");
      networkConfigurator_.setMode(NetworkConfigurator.RDV_SERVER |
          NetworkConfigurator.RDV_CLIENT | NetworkConfigurator.RELAY_CLIENT |
          NetworkConfigurator.RELAY_SERVER | NetworkConfigurator.EDGE_NODE);
      networkConfigurator_.setRendezvousSeedingURIs(seedingURIs);
      networkConfigurator_
          .setRelaySeedingURIs(new HashSet<String>(seedingURIs));
      logger_.info("Principal: " + networkConfigurator_.getPrincipal());
      logger_.info("Password : " + networkConfigurator_.getPassword());
      try {
        logger_.info("Saving new configuration");
        networkConfigurator_.save();
        logger_.info("New configuration saved successfully");
      } catch (IOException ex) {
        ex.printStackTrace();
        System.exit(-1);
      }
    }

    logger_.info("PeerID: " + networkManager_.getPeerID().toString());

    startNetworkManager();

    // socket server
    socketServer_ = new SocketServer(networkManager_.getNetPeerGroup());
    new Thread(socketServer_, "Nebulostore.Communication.SocketServer").start();

    // periodic worker prepartions
    discoveryService_.addDiscoveryListener(this);

    knownPeers_ = new LinkedList<String>();

    messengerServiceInQueue_ = new LinkedBlockingQueue<Message>();

    messengerService_ = new MessengerService(messengerServiceInQueue_,
        outQueue_, networkManager_.getNetPeerGroup(), discoveryService_);

    (new Thread(messengerService_, "Nebulostore.Communication.MessengerService"))
        .start();

    peerDiscoveryService_ = new PeerDiscoveryService(discoveryService_);
    (new Thread(peerDiscoveryService_,
        "Nebulostore.Communication.PeerDiscoveryService")).start();

    logger_.info("fully initialised");
  }

  private void startNetworkManager() {
    try {
      networkManager_.startNetwork();
    } catch (PeerGroupException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    PeerGroup netPeerGroup = networkManager_.getNetPeerGroup();

    // get the discovery service
    discoveryService_ = netPeerGroup.getDiscoveryService();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.nebulostore.appcore.IModule#processMessage(org
   * .nebulostore.appcore.Message)
   */
  private static final long ITERATIONS = 10;
  private static final int PAYLOAD_SIZE = 1024;

  @Override
  protected void processMessage(Message msg) {

    if (msg instanceof CommMessage) {
      logger_.debug("message of class: " + msg.getClass() +
          " forwarded to MessengerService");
      ((CommMessage) msg).setSourceAddress(getPeerAddress());
      messengerServiceInQueue_.add(msg);
    }

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
    logger_.debug("DiscoveryEvent: " + ev.getQueryID());

    DiscoveryResponseMsg res = ev.getResponse();

    if (!knownPeers_.contains("" + ev.getSource())) {
      knownPeers_.add("" + ev.getSource());
      logger_.info("new peer found with address: " + ev.getSource());

      try {
        // TODO: move this URI mod to communication.utils
        outQueue_.add(new CommPeerFoundMessage(
            new CommAddress(PeerID.create(new URI("urn:" +
                ("" + ev.getSource()).replace("//", "")))), getPeerAddress()));
      } catch (URISyntaxException e) {
        logger_.error("URISyntaxException", e);
      }
      logger_.debug("out queue added");
    }
  }

  public CommAddress getPeerAddress() {
    return new CommAddress(networkManager_.getPeerID());
  }

  public PeerDiscoveryService getPeerDiscoveryService() {
    return peerDiscoveryService_;
  }
}
