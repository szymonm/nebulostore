package org.nebulostore.communication;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.security.cert.CertificateException;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.impl.pipe.BlockingWireOutputPipe;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.jxta.MessengerService;
import org.nebulostore.crypto.CryptoUtils;

public class JxtaTest implements DiscoveryListener {

  private static Logger logger_ = Logger.getLogger(JxtaTest.class);
  private final boolean isSuper_;

  private static final int PEER_ROLES_SUPER_PEER = NetworkConfigurator.RDV_CLIENT |
  NetworkConfigurator.RDV_SERVER | NetworkConfigurator.EDGE_NODE;

  private static final int PEER_ROLES_EDGE = NetworkConfigurator.EDGE_NODE;

  private final String bootstrapUrl_ = "http://students.mimuw.edu.pl/~mw262460/bootstrap_jxta.rdv";
  private final int port_ = 9799;

  private transient NetworkManager networkManager_;
  private transient DiscoveryService discoveryService_;

  private transient NetworkConfigurator networkConfigurator_;
  private LinkedList<String> seedingURIs_;
  private final Set<PeerID> knownPeers_;

  private static String peerName_ = CryptoUtils.getRandomId().toString();
  private InputPipe inputPipe_;
  private final Timer sendTimer_;
  private PeerGroup peerGroup_;

  public JxtaTest(boolean isSuper) {
    isSuper_ = isSuper;
    knownPeers_ = new HashSet<PeerID>();
    sendTimer_ = new Timer();
  }

  /**
   * @param args
   * @throws NebuloException
   */
  public static void main(String[] args) throws NebuloException {

    DOMConfigurator.configure("resources/conf/log4j.xml");

    boolean isSuper = false;
    if (args.length == 1)
      isSuper = true;
    JxtaTest jxtaTest = new JxtaTest(isSuper);
    jxtaTest.run();

  }

  private void run() throws NebuloException {
    startJxta();

    while (true) {
      net.jxta.endpoint.Message m = null;
      try {
        m = inputPipe_.waitForMessage();
      } catch (InterruptedException t) {
        logger_.error("error in accept serverPipe", t);
        t.printStackTrace();
      }
      if (m != null) {
        countR++;
        bytesR += (m.getMessageElement("serialized").toString().length());
        // logger_.info("Got message of contents: " +
        // m.getMessageElement("serialized").toString());
      }
    }

  }

  private void startJxta() throws NebuloException {
    initNetworkManager();
    startNetworkManager();
    logger_.info("Network manager started");
    try {
      inputPipe_ = networkManager_.getNetPeerGroup().getPipeService()
      .createInputPipe(MessengerService.getPipeAdvertisement());
    } catch (IOException e) {
      logger_.error(e);
    }
    peerGroup_ = networkManager_.getNetPeerGroup();
    discoveryService_.addDiscoveryListener(this);
    sendTimer_.schedule(new SendTask(), 250, 250);
    sendTimer_.schedule(new StatTask(), 10000, 10000);
    lastTime = System.currentTimeMillis();
  }

  private long count = 0;
  private long lastCount = 0;

  private long bytesSent = 0;
  private long lastBytes = 0;

  private long lastCountR = 0;
  private long countR = 0;

  private long bytesR = 0;
  private long lastBytesR = 0;

  private long lastTime;

  class StatTask extends TimerTask {

    @Override
    public void run() {
      long now = System.currentTimeMillis();
      int peers = 0;
      synchronized (knownPeers_) {
        peers = knownPeers_.size();
      }

      logger_.info("STATS: peers: " + peers + " sent msg : " +
          ((count - lastCount) * 1000 / (now - lastTime)) + " bytes: " +
          ((bytesSent - lastBytes) * 1000 / (now - lastTime)) + " recv msg: " +
          ((countR - lastCountR) * 1000 / (now - lastTime)) + " bytes: " +
          ((bytesR - lastBytesR) * 1000 / (now - lastTime)));
      lastTime = now;
      lastCount = count;
      lastBytes = bytesSent;
      lastCountR = countR;
      lastBytesR = bytesR;
    }

  }

  class SendTask extends TimerTask {

    private int epochesCount = 0;

    @Override
    public void run() {

      epochesCount++;

      String payload = "012345678901";
      for (int i = 0; i < 7; i++) {
        payload += payload;
      }

      int peersSubsetSize = 6;
      int messagesPerPeer = 2;

      Random rand = new Random(System.currentTimeMillis());

      synchronized (knownPeers_) {
        logger_.info("Sender awaken: knownPeers_: " + knownPeers_);

        if (knownPeers_.size() >= peersSubsetSize) {

          Set<PeerID> knownPeersCopy_ = new HashSet<PeerID>();
          Vector<PeerID> all = new Vector(knownPeers_);
          for (int i = 0; i < peersSubsetSize; i++) {
            knownPeersCopy_.add(all.remove(rand.nextInt(all.size())));
          }

          for (PeerID dest : knownPeersCopy_) {
            for (int i = 0; i < messagesPerPeer; i++) {
              count++;
              OutputPipe pipe = new BlockingWireOutputPipe(peerGroup_,
                  MessengerService.getPipeAdvertisement(), dest);
              net.jxta.endpoint.Message jxtaMessage = new net.jxta.endpoint.Message();
              String data = "Hello from: " + networkManager_.getPeerID() +
              " count: " + count + payload;
              bytesSent += data.length();
              jxtaMessage.addMessageElement(new StringMessageElement(
                  "serialized", data, null));
              try {
                pipe.send(jxtaMessage);
              } catch (IOException e) {
                logger_.error(e);
              }

            }
          }
        }
      }
      if (epochesCount % (4 * 10) == 0) {
        try {
          discoveryService_.publish(MessengerService.getPipeAdvertisement(),
              30000, 30000);
        } catch (IOException e) {
          logger_.error(e);
        }
        discoveryService_.getRemoteAdvertisements(null, DiscoveryService.ADV,
            null, null, 30);
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
    discoveryService_ = netPeerGroup.getDiscoveryService();
  }

  private void initNetworkManager() throws NebuloException {

    logger_.info("Getting NetworkManager instance.");
    try {
      networkManager_ = new NetworkManager(NetworkManager.ConfigMode.EDGE,
          peerName_);
    } catch (IOException e) {
      logger_.fatal(e);
      System.exit(-1);
    }
    logger_.info("Setting persistent config on network manager instance: " +
        networkManager_);

    networkManager_.setConfigPersistent(true);
    // logger_.info("PeerID: " + networkManager_.getPeerID().toString());

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

      networkConfigurator_.setName(CryptoUtils.getRandomId().toString());
      networkConfigurator_.setTcpPort(port_);
      networkConfigurator_.setRendezvousMaxClients(13);

      if (isSuper_) {
        networkConfigurator_.setMode(PEER_ROLES_SUPER_PEER);
      } else {
        networkConfigurator_.setMode(PEER_ROLES_EDGE);
      }

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

  @Override
  public void discoveryEvent(DiscoveryEvent ev) {
    logger_.info("DiscoveryEvent received with address: " + ev.getSource());

    synchronized (knownPeers_) {

      try {
        // TODO: move this URI mod to communication.utils
        knownPeers_.add(PeerID.create(new URI("urn:" +
            ("" + ev.getSource()).replace("//", ""))));

      } catch (URISyntaxException e) {
        logger_.error("URISyntaxException", e);
      }
    }

  }
}
