package org.nebulostore.communication;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.bdbdht.BdbPeer;
import org.nebulostore.communication.bootstrap.BootstrapClient;
import org.nebulostore.communication.exceptions.CommException;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.DiscoveryMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;
import org.nebulostore.communication.messages.ReconfigureDHTAckMessage;
import org.nebulostore.communication.messages.ReconfigureDHTMessage;
import org.nebulostore.communication.messages.bdbdht.BdbMessageWrapper;
import org.nebulostore.communication.messages.bdbdht.HolderAdvertisementMessage;
import org.nebulostore.communication.messages.dht.DHTMessage;
import org.nebulostore.communication.messages.dht.InDHTMessage;
import org.nebulostore.communication.messages.dht.OutDHTMessage;
import org.nebulostore.communication.socket.ListenerService;
import org.nebulostore.communication.socket.MessengerService;

//TODO-GM add kademlia support
/**
 * Main module for communication with outside world.
 *
 * So far it only handles BDB.
 * @author Marcin Walas
 * @author Grzegorz Milka
 */
public class CommunicationPeer extends Module {
  private static Logger logger_ = Logger.getLogger(CommunicationPeer.class);
  private static final String CONFIGURATION_PATH =
    "resources/conf/communication/CommunicationPeer.xml";

  private Module dhtPeer_;
  private Thread dhtPeerThread_;

  private BlockingQueue<Message> bootstrapInQueue_;
  private BlockingQueue<Message> messengerServiceInQueue_;
  private final BlockingQueue<Message> dhtInQueue_;

  private static BootstrapClient bootstrapClient_;
  private ListenerService listenerService_;
  private MessengerService messengerService_;

  private int commCliPort_ = 9987;
  private static final int MIN_LISTENER_PORT = 9970;

  public CommunicationPeer(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue) throws NebuloException {
    super(inQueue, outQueue);
    logger_.debug("Starting CommunicationPeer");

    XMLConfiguration config = null;
    try {
      config = new XMLConfiguration(CONFIGURATION_PATH);
    } catch (ConfigurationException cex) {
      logger_.error("Configuration read error in: " + CONFIGURATION_PATH);
    }

    bootstrapInQueue_ = new LinkedBlockingQueue<Message>();
    messengerServiceInQueue_ = new LinkedBlockingQueue<Message>();
    dhtInQueue_ = new LinkedBlockingQueue<Message>();

    //Init listener, if you can't try different ports 
    try {
      listenerService_ = new ListenerService(inQueue_);
    } catch(IOException e) {
      for(; commCliPort_ >= MIN_LISTENER_PORT; --commCliPort_) {
        try {
          listenerService_ = new ListenerService(inQueue_);
          break;
        } catch(IOException err) {
          logger_.error("Couldn't initialize listener with port: " +
              commCliPort_ + " " + err);
          if(commCliPort_ == MIN_LISTENER_PORT)
            throw new NebuloException("Couldn't initialize listener.", err);
        }
      }
    }

    bootstrapClient_ = new BootstrapClient(bootstrapInQueue_, inQueue_, commCliPort_);

    try {
      messengerService_ = new MessengerService(messengerServiceInQueue_, inQueue_);
    } catch (IOException e) {
      logger_.error("Couldn't initialize sender: " + e);
      throw new NebuloException("Couldn't initialize sender.", e);
    }


    //NOTE-GM: why naming like here and not org.nebulostore...
    //NOTE-GM: Used thread, because it's copied, but Executor might be more OO
    new Thread(bootstrapClient_, "Nebulostore.Communication.Bootstrap").start();
    new Thread(messengerService_, "Nebulostore.Communication.MessengerService").start();
    new Thread(listenerService_, "Nebulostore.Communication.ListenerService").start();

    if (!config.getString("dht.provider", "bdb").equals("none")) {
      reconfigureDHT(config.getString("dht.provider", "bdb"), null);
    } else {
      dhtPeer_ = null;
    }
  }

  public static CommAddress getPeerAddress() {
    return bootstrapClient_.getPeerAddress();
  }

  public Module getDHTPeer() {
    logger_.error("DHTPeer unsupported yet");
    throw new UnsupportedOperationException();
  }

  @Override
  protected void processMessage(Message msg) {
    logger_.debug("Processing message: " + msg);

    if (msg instanceof ErrorCommMessage) {
      logger_.info("Error comm message. Returning it to Dispatcher");
      outQueue_.add(msg);
      return;
    }

    if (msg instanceof ReconfigureDHTMessage) {
      try {
        logger_.info("Got reconfigure request with jobId: " + msg.getId());
        reconfigureDHT(((ReconfigureDHTMessage) msg).getProvider(),
            (ReconfigureDHTMessage) msg);
      } catch (NebuloException e) {
        logger_.error(e);
      }
      return;
    }

    if (msg instanceof HolderAdvertisementMessage) {
      dhtInQueue_.add(msg);
    }

    if (msg instanceof DiscoveryMessage) {
      bootstrapInQueue_.add(msg);
      return;
    }

    if (msg instanceof CommPeerFoundMessage) {
      logger_.debug("CommPeerFound message forwarded to Dispatcher");
      outQueue_.add(msg);
      return;
    }

    if (msg instanceof DHTMessage) {
      if (msg instanceof InDHTMessage) {
        logger_.debug("InDHTMessage forwarded to DHT");
        dhtInQueue_.add(msg);
      }
      else if (msg instanceof OutDHTMessage) {
        logger_.debug("OutDHTMessage forwarded to Dispatcher");
        outQueue_.add(msg);
      }
      else {
        logger_.error("Unrecognized DHTMessage: " + msg);
      }
      return;
    }

    if (msg instanceof BdbMessageWrapper) {
      logger_.debug("BDB DHT message received");
      BdbMessageWrapper casted = (BdbMessageWrapper) msg;
      if (casted.getWrapped() instanceof InDHTMessage) {
        logger_.debug("BDB DHT message forwarded to DHT");
        dhtInQueue_.add(casted.getWrapped());
      }
      else if (casted.getWrapped() instanceof OutDHTMessage) {
        logger_.debug("BDB DHT message forwarded to Dispatcher");
        outQueue_.add(casted.getWrapped());
      }
      else {
        logger_.error("Unrecognized BdbMessageWrapper: " + msg);
      }
      return;
    }

    if (msg instanceof CommMessage) {
      if (((CommMessage) msg).getSourceAddress() == null) {
        ((CommMessage) msg).setSourceAddress(getPeerAddress());
      }

      if (((CommMessage) msg).getDestinationAddress() == null) {
        logger_.error("Null destination address set for " + msg + ". Dropping the message.");
        return;
      }

      if (((CommMessage) msg).getDestinationAddress().equals(
            bootstrapClient_.getPeerAddress())) {
        logger_.debug("message forwarded to Dispatcher");
        outQueue_.add(msg);
      } else {
        logger_.debug("message forwarded to MessengerService");
        messengerServiceInQueue_.add(msg);
      }
      return;
    }

    logger_.warn("Unrecognized message of type " + msg.toString());

  }

  private void reconfigureDHT(String dhtProvider,
      ReconfigureDHTMessage reconfigureRequest) throws NebuloException {

    if (dhtProvider.equals("bdb") && (dhtPeer_ instanceof BdbPeer)) {
      if (reconfigureRequest != null && ((BdbPeer) dhtPeer_).getHolderAddress() != null) {
        outQueue_.add(new ReconfigureDHTAckMessage(reconfigureRequest));
      }
      return;
    }
    else {
      if (dhtPeerThread_ != null) {
        dhtPeer_.endModule();
        dhtPeerThread_.interrupt();
      }

      if (dhtProvider.equals("bdb")) {
        dhtPeer_ = new BdbPeer(dhtInQueue_, outQueue_, getPeerAddress(),
            messengerServiceInQueue_, reconfigureRequest);
      } else {
        throw new CommException("Unsupported DHT Provider in configuration");
      }
      dhtPeerThread_ = new Thread(dhtPeer_, "Nebulostore.Communication.DHT");
      dhtPeerThread_.start();
    }
  }
}
