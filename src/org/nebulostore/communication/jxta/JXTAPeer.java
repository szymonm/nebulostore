package org.nebulostore.communication.jxta;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.security.cert.CertificateException;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.exception.PeerGroupException;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.socket.JxtaSocket;

import org.apache.log4j.Logger;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.CommSendDataMessage;

/**
 * @author Marcin Walas
 */
public class JXTAPeer extends Module implements DiscoveryListener {

  private static String peerName_ = "mypeername";

  private transient MessengerService messengerService_;

  private transient SocketServer socketServer_;

  private transient NetworkManager networkManager_;
  private transient DiscoveryService discoveryService_;

  private transient NetworkConfigurator networkConfigurator_;

  private final BlockingQueue<Message> messengerServiceInQueue_;

  private static Logger logger_ = Logger.getLogger(JXTAPeer.class);

  private final List<String> knownPeers_;

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
      networkConfigurator_.setName(peerName_);
      networkConfigurator_.setPrincipal("asdf");
      networkConfigurator_.setPassword("asfd");
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

    (new Thread(messengerService_, "Nebulostore.Communication.PeriodicWorker"))
        .start();

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
   * @see
   * org.nebulostore.appcore.IModule#processMessage(org
   * .nebulostore.appcore.Message)
   */
  private static final long ITERATIONS = 10;
  private static final int PAYLOAD_SIZE = 1024;

  @Override
  protected void processMessage(Message msg) {

    if (msg instanceof CommMessage) {
      logger_.debug("message forwarded to MessengerService");
      ((CommMessage) msg).setSourceAddress(getPeerAddress());
      messengerServiceInQueue_.add(msg);
    }

    // TODO(MBW) : DEPRECATED - Remove this
    if (msg instanceof CommSendDataMessage) {
      try {

        logger_.info("Processing MsgCommSendData");

        long start = System.currentTimeMillis();

        JxtaSocket socket = null;

        try {
          socket = new JxtaSocket(networkManager_.getNetPeerGroup(),
              // no specific peerid
              PeerID.create(new URI("urn:" +
                  ((CommSendDataMessage) msg).address_.replace("//", ""))),
              SocketServer.createSocketAdvertisement(),
              // connection timeout: 5 seconds
              5000,
              // reliable connection
              true);

        } catch (URISyntaxException e) {
          logger_.error("Exception: ", e);
        }
        // get the socket output stream
        OutputStream out = socket.getOutputStream();
        DataOutput dos = new DataOutputStream(out);

        // get the socket input stream
        InputStream in = socket.getInputStream();
        DataInput dis = new DataInputStream(in);

        long total = ITERATIONS * PAYLOAD_SIZE * 2;
        logger_.info("Sending/Receiving " + total + " bytes.");

        dos.writeLong(ITERATIONS);
        dos.writeInt(PAYLOAD_SIZE);

        long current = 0;

        while (current < ITERATIONS) {
          byte[] outBuf = new byte[PAYLOAD_SIZE];
          byte[] inBuf = new byte[PAYLOAD_SIZE];

          Arrays.fill(outBuf, (byte) current);
          out.write(outBuf);
          out.flush();
          dis.readFully(inBuf);
          assert Arrays.equals(inBuf, outBuf);
          current++;
        }
        out.close();
        in.close();

        long finish = System.currentTimeMillis();
        long elapsed = finish - start;

        logger_.info(MessageFormat.format(
            "EOT. Processed {0} bytes in {1} ms. Throughput = {2} KB/sec.",
            total, elapsed, (total / elapsed) / 1024));
        socket.close();
        logger_.info("Socket connection closed");

      } catch (IOException e) {
        logger_.error("Exception: ", e);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * net.jxta.discovery.DiscoveryListener#discoveryEvent(net.jxta.discovery.
   * DiscoveryEvent)
   */

  @Override
  public void discoveryEvent(DiscoveryEvent ev) {
    DiscoveryResponseMsg res = ev.getResponse();

    // let's get the responding peer's advertisement
    logger_.info(" [  Got a Discovery Response [" + res.getResponseCount() +
        " elements]  from peer : " + ev.getSource() + "  ]");

    if (!knownPeers_.contains("" + ev.getSource())) {
      knownPeers_.add("" + ev.getSource());
      logger_.info("known peers: " + knownPeers_);
      logger_.info("peer added!");

      try {
        outQueue_.add(new CommPeerFoundMessage(new CommAddress(PeerID.create(new URI(
            "urn:" + ("" + ev.getSource()).replace("//", "")))), getPeerAddress()));
      } catch (URISyntaxException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      logger_.info("out queue added");
    }

    Advertisement adv;
    Enumeration en = res.getAdvertisements();

    if (en != null) {
      while (en.hasMoreElements()) {
        adv = (Advertisement) en.nextElement();
        logger_.info(adv);
      }
    }

  }

  public CommAddress getPeerAddress() {
    // TODO Auto-generated method stub
    return new CommAddress(networkManager_.getPeerID());
  }
}
