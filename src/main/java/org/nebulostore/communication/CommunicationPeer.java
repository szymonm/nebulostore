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
//import org.nebulostore.communication.bdbdht.BdbPeer;
import org.nebulostore.communication.bootstrap.BootstrapClient;
import org.nebulostore.communication.exceptions.CommException;
//import org.nebulostore.communication.kademlia.KademliaPeer;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.DiscoveryMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;
import org.nebulostore.communication.messages.ReconfigureDHTMessage;
import org.nebulostore.communication.messages.bdbdht.HolderAdvertisementMessage;
//import org.nebulostore.communication.messages.ReconfigureDHTAckMessage;
//import org.nebulostore.communication.messages.bdbdht.BdbMessageWrapper;
//import org.nebulostore.communication.messages.dht.DHTMessage;
//import org.nebulostore.communication.messages.dht.InDHTMessage;
//import org.nebulostore.communication.messages.dht.OutDHTMessage;
//import org.nebulostore.communication.messages.kademlia.KademliaMessage;
//import org.nebulostore.communication.messages.streambinding.ErrorStreamBindingMessage;
//import org.nebulostore.communication.messages.streambinding.StreamBindingMessage;
//import org.nebulostore.communication.messages.streambinding.StreamBindingReadyMessage;
import org.nebulostore.communication.socket.ListenerService;
import org.nebulostore.communication.socket.MessengerService;
//import org.nebulostore.communication.streambinding.StreamBindingService;

/**
 * @author Marcin Walas
 * @author Grzegorz Milka
 */
public class CommunicationPeer extends Module {
  private static Logger logger_ = Logger.getLogger(CommunicationPeer.class);
  private static final String CONFIGURATION_PATH =
    "resources/conf/communication/CommunicationPeer.xml";

  private BlockingQueue<Message> bootstrapInQueue_;
  private BlockingQueue<Message> messengerServiceInQueue_;

  private static BootstrapClient bootstrapClient_;
  private ListenerService listenerService_;
  private MessengerService messengerService_;

  public CommunicationPeer(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue) throws NebuloException {
    super(inQueue, outQueue);

    XMLConfiguration config = null;
    try {
      config = new XMLConfiguration(CONFIGURATION_PATH);
    } catch (ConfigurationException cex) {
      logger_.error("Configuration read error in: " + CONFIGURATION_PATH);
    }

    bootstrapInQueue_ = new LinkedBlockingQueue<Message>();
    messengerServiceInQueue_ = new LinkedBlockingQueue<Message>();

    bootstrapClient_ = new BootstrapClient(bootstrapInQueue_, inQueue);
    try {
      messengerService_ = new MessengerService(messengerServiceInQueue_, inQueue);
    } catch (IOException e) {
      logger_.error("Couldn't initialize sender " + e);
      throw new NebuloException("Couldn't initialize sender." ,e);
    }

    listenerService_ = new ListenerService(inQueue);

    //NOTE: why naming like here and not org.nebulostore...
    new Thread(bootstrapClient_, "Nebulostore.Communication.Bootstrap").start();
    new Thread(messengerService_, "Nebulostore.Communication.MessengerService").start();
    new Thread(listenerService_, "Nebulostore.Communication.ListenerService").start();
  }

  @Override
  protected void processMessage(Message msg) {
    logger_.debug("Processing message..");

    if (msg instanceof ErrorCommMessage) {
      logger_.info("Error comm message. Returning it to Dispatcher");
      outQueue_.add(msg);
      return;
    }

    if (msg instanceof ReconfigureDHTMessage) {
      logger_.info("Got reconfigure request with jobId: " + msg.getId());
      return;
    }

    if (msg instanceof HolderAdvertisementMessage) {
      //dhtInQueue_.add(msg);
      //Do nothing
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

    /*
       if (msg instanceof DHTMessage) {
       if (msg instanceof InDHTMessage) {
       logger_.debug("InDHTMessage forwarded to DHT");
       dhtInQueue_.add(msg);
       }
       if (msg instanceof OutDHTMessage) {
       logger_.debug("OutDHTMessage forwarded to Dispatcher");
       outQueue_.add(msg);
       }
       return;
       }

       if (msg instanceof KademliaMessage) {
       logger_.debug("Kademlia DHT message received");
       dhtInQueue_.add(msg);
       return;
       }

       if (msg instanceof StreamBindingMessage) {
       logger_.debug("Stream binding message received");
       streamBindingInQueue_.add(msg);
       return;
       }

       if (msg instanceof StreamBindingReadyMessage) {
       logger_.debug("Stream binding ready message received");
       outQueue_.add(msg);
       return;
       }

       if (msg instanceof ErrorStreamBindingMessage) {
       logger_.debug("Stream binding error message received");
       outQueue_.add(msg);
       return;
       }

       if (msg instanceof BdbMessageWrapper) {
       logger_.debug("BDB DHT message received");
       BdbMessageWrapper casted = (BdbMessageWrapper) msg;
       if (casted.getWrapped() instanceof InDHTMessage) {
       logger_.debug("BDB DHT message forwarded to DHT");
       dhtInQueue_.add(msg);
       }
       if (casted.getWrapped() instanceof OutDHTMessage) {
       logger_.debug("BDB DHT message forwarded to Dispatcher");
       outQueue_.add(casted.getWrapped());
       }
       return;
       }*/

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
        logger_.debug("message forwarded to jxtaPeer");
        messengerServiceInQueue_.add(msg);
      }
      return;
    }

    logger_.warn("Unrecognized message of type " + msg.toString());

  }

  public static CommAddress getPeerAddress() {
    return bootstrapClient_.getPeerAddress();
  }

  public Module getDHTPeer() {
    logger_.error("DHTPeer unsupported yet");
    throw new UnsupportedOperationException();
  }
}
