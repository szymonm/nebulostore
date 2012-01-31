package org.nebulostore.communication.bdbdht;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.concurrent.BlockingQueue;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Durability;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.ID;
import net.jxta.peer.PeerID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.dht.ValueDHT;
import org.nebulostore.communication.jxta.PeerDiscoveryService;
import org.nebulostore.communication.messages.bdbdht.BdbMessageWrapper;
import org.nebulostore.communication.messages.dht.DHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;

/**
 * Implementation of berkely db based engine for...
 *
 * @author marcin
 */
public class BdbPeer extends Module implements DiscoveryListener {

  private static String configurationPath_ = "resources/conf/communication/BdbPeer.xml";
  private static Logger logger_ = Logger.getLogger(BdbPeer.class);

  private static final String BDB_HOLDER_ADV_ID_STR = "urn:jxta:" +
      "uuid-59616261646162614E504720503250338944BCED387C4A2BBD8E9411B78C28FF04";

  private String storagePath_;
  private String storeName_;
  private Database database_;
  private Environment env_;

  private boolean isProxy_;
  /**
   */
  private CommAddress holderCommAddress_;

  private final BlockingQueue<Message> jxtaInQueue_;

  /**
   * @param inQueue
   * @param outQueue
   * @param peerDiscoveryService
   * @param jxtaInQueue
   *          queue to jxtaPeer module for direct messages sending
   */
  public BdbPeer(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue,
      PeerDiscoveryService peerDiscoveryService,
      BlockingQueue<Message> jxtaInQueue) {
    super(inQueue, outQueue);

    jxtaInQueue_ = jxtaInQueue;

    // TODO: move to factory in appcore
    XMLConfiguration config = null;
    try {
      config = new XMLConfiguration(configurationPath_);
    } catch (ConfigurationException cex) {
      logger_.error("Configuration read error in: " + configurationPath_);
    }
    if (config.getString("type", "proxy").equals("storage-holder")) {

      logger_.info("Configuring as bdb database holder");

      storagePath_ = config.getString("sleepycat.storage-path",
          "/tmp/nebulostore-bdb");

      storeName_ = config.getString("sleepycat.store-name", "nebulostore-bdb");

      logger_.debug("storagePath: " + storagePath_);

      env_ = createEnvironment(new File(storagePath_));

      DatabaseConfig dbConfig = new DatabaseConfig();
      dbConfig.setTransactional(true);
      dbConfig.setAllowCreate(true);

      database_ = env_.openDatabase(null, storeName_, dbConfig);

      peerDiscoveryService.addAdvertisement(getBdbAdvertisement());
    } else {
      logger_.info("Configuring as proxy");
      isProxy_ = true;
      peerDiscoveryService.getDiscoveryService().addDiscoveryListener(this);
    }
    logger_.info("fully initialized");

  }

  private Environment createEnvironment(File dir) {
    EnvironmentConfig config = new EnvironmentConfig();
    config.setAllowCreate(true);
    config.setDurability(Durability.COMMIT_SYNC);

    config.setTransactional(true);
    config.setTxnSerializableIsolation(true);

    Environment env = new Environment(dir, config);
    return env;
  }

  public static PipeAdvertisement getBdbAdvertisement() {
    PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
        .newAdvertisement(PipeAdvertisement.getAdvertisementType());

    advertisement.setPipeID(ID.create(URI.create(BDB_HOLDER_ADV_ID_STR)));
    advertisement.setType(PipeService.UnicastType);
    advertisement.setName("Nebulostore bdb dht holder");
    return advertisement;
  }

  @Override
  protected void processMessage(Message msg) throws NebuloException {
    logger_.info("Message accepted.");
    Message message;

    if (isProxy_) {
      logger_.info("Putting message to be sent to holder (taskId = " + msg.getId() + ")");
      jxtaInQueue_.add(new BdbMessageWrapper(null, holderCommAddress_, (DHTMessage) msg));
    } else {
      boolean fromNetwork = false;
      if (msg instanceof BdbMessageWrapper) {
        message = ((BdbMessageWrapper) msg).getWrapped();
        fromNetwork = true;
      } else {
        message = msg;
      }

      if (message instanceof PutDHTMessage) {

        PutDHTMessage putMsg = (PutDHTMessage) message;
        logger_.info("PutDHTMessage (" + putMsg.getId() + ") in holder with " +
            putMsg.getKey().toString() + " : " + putMsg.getValue().toString());

        String key = putMsg.getKey().toString();
        String value = putMsg.getValue().serializeValue();
        // TODO: Serialization error handling as well
        Transaction t = env_.beginTransaction(null, null);
        database_.put(t, new DatabaseEntry(key.getBytes()), new DatabaseEntry(
            value.getBytes()));

        t.commit();
        if (fromNetwork) {
          jxtaInQueue_.add(new BdbMessageWrapper(null, ((BdbMessageWrapper) msg).getSourceAddress(),
              new OkDHTMessage(putMsg)));
        } else {
          outQueue_.add(new OkDHTMessage(putMsg));
        }

        logger_.info("PutDHTMessage processing finished");
      } else if (message instanceof GetDHTMessage) {
        logger_.info("GetDHTMessage in holder");
        GetDHTMessage getMsg = (GetDHTMessage) message;

        KeyDHT key = getMsg.getKey();
        DatabaseEntry data = new DatabaseEntry();

        if (database_.get(null, new DatabaseEntry(key.toString().getBytes()),
            data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
          ValueDHT value = ValueDHT.build(new String(data.getData()));

          if (fromNetwork) {
            jxtaInQueue_.add(new BdbMessageWrapper(null,
                ((BdbMessageWrapper) msg).getSourceAddress(),
                new ValueDHTMessage(getMsg, key, value)));
          } else {
            outQueue_.add(new ValueDHTMessage(getMsg, key, value));
          }

        } else {
          // TODO: Error handling
          logger_
              .error("Unable to read from database. Should send an ErrorDHTMessage back");
        }
        logger_.info("GetDHTMessage processing finished");
      } else {
        logger_.error("BdbPeer got message that is not supported");
      }
    }
  }

  @Override
  public void discoveryEvent(DiscoveryEvent ev) {
    logger_.debug("DiscoveryEvent: " + ev.getQueryID());

    Advertisement adv;
    Enumeration<Advertisement> en = ev.getResponse().getAdvertisements();

    if (en != null) {
      while (en.hasMoreElements()) {
        adv = (Advertisement) en.nextElement();

        String id = adv.getID() == null ? "null" : adv.getID().toString();
        if (BDB_HOLDER_ADV_ID_STR.equals(id) /*&& holderCommAddress_ == null*/) {
          try {
            holderCommAddress_ = new CommAddress(PeerID.create(new URI("urn:" +
                ("" + ev.getSource()).replace("//", ""))));
            logger_.info("Holder detected at " + holderCommAddress_.toString());
          } catch (URISyntaxException e) {
            logger_.error("", e);
          }
        }
      }
    } else {
      logger_.debug("discoveryEvent empty...");
    }

  }
}
