package org.nebulostore.communication.dht;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.inject.Inject;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Durability;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.appcore.EndModuleMessage;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.ReconfigureDHTAckMessage;
import org.nebulostore.communication.messages.ReconfigureDHTMessage;
import org.nebulostore.communication.messages.bdbdht.BdbMessageWrapper;
import org.nebulostore.communication.messages.bdbdht.HolderAdvertisementMessage;
import org.nebulostore.communication.messages.dht.DHTMessage;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.OutDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.networkmonitor.NetworkContext;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of berkely db based engine for...
 * @author marcin
 */
public class BdbPeer extends Module {
  private static Logger logger_ = Logger.getLogger(BdbPeer.class);
  private static final String CONFIG_PREFIX = "communication.dht.bdb-peer.";

  private String storagePath_;
  private String storeName_;
  private Database database_;
  private Environment env_;

  private boolean isProxy_;
  private CommAddress holderCommAddress_;
  private final CommAddress bdbHolderCommAddress_ = null;

  private final BlockingQueue<Message> senderInQueue_;
  private final ReconfigureDHTMessage reconfigureRequest_;
  private Timer advertisementsTimer_;
  private XMLConfiguration config_;
  private Queue<Message> messageCache_ = new LinkedBlockingQueue<Message>();
  private MessageVisitor<Void> msgVisitor_;
  private CommAddress commAddress_;

  @Inject
  public void setConfig(XMLConfiguration config) {
    config_ = config;
  }

  @Inject
  public void setCommAddress(CommAddress commAddress) {
    commAddress_ = commAddress;
  }

  /**
   * @param inQueue
   * @param outQueue
   * @param senderInQueue Queue to module for direct sending of messages to
   * other peers
   */
  public BdbPeer(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue,
      BlockingQueue<Message> senderInQueue,
      ReconfigureDHTMessage reconfigureRequest) {
    super(inQueue, outQueue);
    senderInQueue_ = senderInQueue;
    reconfigureRequest_ = reconfigureRequest;
  }

  @Override
  public void run() {
    initPeer();
    super.run();
  }

  private void initPeer() {
    checkNotNull(config_);
    if (config_.getString(CONFIG_PREFIX + "type", "proxy").equals("storage-holder")) {
      logger_.info("Configuring as bdb database holder");

      msgVisitor_ = new BDBServerMessageVisitor();
      storagePath_ = config_.getString(CONFIG_PREFIX + "sleepycat.storage-path");
      storeName_ = config_.getString(CONFIG_PREFIX + "sleepycat.storage-name");
      checkNotNull(storagePath_);
      checkNotNull(storeName_);
      logger_.debug("storagePath: " + storagePath_);
      logger_.debug("storeName: " + storeName_);

      env_ = createEnvironment(new File(storagePath_));

      DatabaseConfig dbConfig = new DatabaseConfig();
      dbConfig.setTransactional(true);
      dbConfig.setAllowCreate(true);

      database_ = env_.openDatabase(null, storeName_, dbConfig);

      advertisementsTimer_ = new Timer(true);
      advertisementsTimer_.schedule(new SendAdvertisement(), 500, 500);

      if (reconfigureRequest_ != null) {
        outQueue_.add(new ReconfigureDHTAckMessage(reconfigureRequest_));
      }

      holderCommAddress_ = commAddress_;
    } else {
      logger_.info("Configuring as proxy");

      msgVisitor_ = new BDBProxyMessageVisitor();
      isProxy_ = true;
      holderCommAddress_ = bdbHolderCommAddress_;
    }
    logger_.info("fully initialized");
  }

  /**
   * Module that sends holder advertisements.
   */
  public class SendAdvertisement extends TimerTask {
    private Set<CommAddress> done_ = new TreeSet<CommAddress>();
    @Override
    public void run() {
      for (CommAddress address : NetworkContext.getInstance().getKnownPeers()) {
        if (!done_.contains(address)) {
          logger_.info("Sending holder advertisement to " + address);
          senderInQueue_.add(new HolderAdvertisementMessage(address));
          done_.add(address);
        }
      }
    }
  }

  private void shutdown() {
    logger_.info("Ending bdb peer");
    if (!isProxy_) {
      advertisementsTimer_.cancel();
      logger_.info("Closing database...");
      database_.close();
      env_.close();
    }

    endModule();
  }

  private Environment createEnvironment(File dir) {
    if (!dir.exists()) {
      logger_.debug("Creating directory: " + dir + " for BdbPeer.");
      boolean mkdirResult = dir.mkdirs();
      if (!mkdirResult) {
        logger_.error("Enviroment directory: " + dir +
            " could not be created.");
        throw new IllegalArgumentException("Enviroment directory: " + dir +
            " could not be created.");
      }
    } else if (dir.exists() && !dir.isDirectory()) {
      logger_.error("Enviroment path: " + dir + " is not a directory.");
      throw new IllegalArgumentException("Enviroment path: " + dir +
          " is not a directory.");
    }

    EnvironmentConfig config = new EnvironmentConfig();
    config.setAllowCreate(true);
    config.setDurability(Durability.COMMIT_SYNC);

    config.setTransactional(true);
    config.setTxnSerializableIsolation(true);

    Environment env = new Environment(dir, config);
    return env;
  }

  private void put(PutDHTMessage putMsg, boolean fromNetwork,
      CommAddress sourceAddress) {
    String srcAddr = sourceAddress == null ? "null" : sourceAddress.toString();
    logger_.info("PutDHTMessage (" + putMsg.getId() + ") in holder with " +
        putMsg.getKey().toString() + " : " + putMsg.getValue().toString() +
        " and sourceAddress = " + srcAddr);

    KeyDHT key = putMsg.getKey();
    ValueDHT valueDHT = putMsg.getValue();

    // TODO: Serialization error handling as well
    Transaction t = env_.beginTransaction(null, null);

    DatabaseEntry data = new DatabaseEntry();
    OperationStatus operationStatus = database_.get(t,
            new DatabaseEntry(key.toString().getBytes(Charset.forName("UTF-8"))),
            data,
            LockMode.DEFAULT);

    if (operationStatus == OperationStatus.SUCCESS) {
      logger_.info("Performing merge on object from DHT");
      ValueDHT oldValue = ValueDHT.build(new String(data.getData(),
            Charset.forName("UTF-8")));
      valueDHT = new ValueDHT(valueDHT.getValue().merge(oldValue.getValue()));
    }

    String value = valueDHT.serializeValue();
    database_.put(t, new DatabaseEntry(key.toString().getBytes(Charset.forName("UTF-8"))),
        new DatabaseEntry(value.getBytes(Charset.forName("UTF-8"))));

    t.commit();
    if (fromNetwork) {
      senderInQueue_.add(new BdbMessageWrapper(null, sourceAddress,
          new OkDHTMessage(putMsg)));
    } else {
      outQueue_.add(new OkDHTMessage(putMsg));
    }
    logger_.info("PutDHTMessage processing finished");
  }

  private void get(GetDHTMessage getMsg, boolean fromNetwork, CommAddress sourceAddress) {
    logger_.debug("GetDHTMessage in holder");
    KeyDHT key = getMsg.getKey();
    DatabaseEntry data = new DatabaseEntry();
    OperationStatus operationStatus = database_.get(null,
        new DatabaseEntry(key.toString().getBytes(Charset.forName("UTF-8"))),
        data, LockMode.DEFAULT);

    OutDHTMessage outMessage;
    if (operationStatus == OperationStatus.SUCCESS) {
      ValueDHT value = ValueDHT.build(new String(data.getData(),
            Charset.forName("UTF-8")));
      outMessage = new ValueDHTMessage(getMsg, key, value);
    } else {
      logger_.debug("Unable to read from database. Sending ErrorDHTMessage.");
      outMessage = new ErrorDHTMessage(getMsg, new NebuloException(
          "Unable to read from bdb database. Operation status: " + operationStatus));
    }

    if (fromNetwork) {
      senderInQueue_.add(new BdbMessageWrapper(null, sourceAddress, outMessage));
    } else {
      outQueue_.add(outMessage);
    }
    logger_.debug("GetDHTMessage processing finished");
  }

  @Override
  protected void processMessage(Message msg) throws NebuloException {
    logger_.debug("Processing message: " + msg);

    if (isProxy_ && holderCommAddress_ == null) {
      logger_.debug("Holder not set up, waiting for HolderAdvertisementMessage");
      messageCache_.add(msg);
      return;
    }

    logger_.info("Message accepted: " + msg);
    msg.accept(msgVisitor_);
  }

  private void cleanCache() throws NebuloException {
    logger_.debug("Cleaning cache.");
    while (!messageCache_.isEmpty())
      processMessage(messageCache_.remove());
    logger_.debug("Finished cleaning cache.");
  }

  public CommAddress getHolderAddress() {
    return holderCommAddress_;
  }

  /**
   * Message Visitor for proxy BDB Peer.
   *
   * @author Grzegorz Milka
   */
  private final class BDBProxyMessageVisitor extends MessageVisitor<Void> {
    @Override
    public Void visit(EndModuleMessage msg) {
      shutdown();
      return null;
    }

    @Override
    public Void visit(DHTMessage msg) {
      logger_.debug("Message accepted. " + msg);
      logger_.debug("Putting message to be sent to holder (taskId = " +
          msg.getId() + ")");
      senderInQueue_.add(new BdbMessageWrapper(null, holderCommAddress_,
          (DHTMessage) msg));
      return null;
    }

    @Override
    public Void visit(Message msg) {
      logger_.warn("Unknown message of type: " + msg);
      return null;
    }
  }

  /**
   * Message Visitor for server BDB Peer.
   *
   * @author Grzegorz Milka
   */
  private final class BDBServerMessageVisitor extends MessageVisitor<Void> {
    @Override
    public Void visit(EndModuleMessage msg) {
      shutdown();
      return null;
    }

    @Override
    public Void visit(BdbMessageWrapper msg) throws NebuloException {
      boolean fromNetwork = true;
      CommAddress sourceAddress = msg.getSourceAddress();
      Message message = msg.getWrapped();

      if (message instanceof PutDHTMessage) {
        put((PutDHTMessage) message, fromNetwork, sourceAddress);
      } else if (message instanceof GetDHTMessage) {
        get((GetDHTMessage) message, fromNetwork, sourceAddress);
      } else {
        message.accept(msgVisitor_);
      }
      return null;
    }

    @Override
    public Void visit(HolderAdvertisementMessage msg) throws NebuloException {
      logger_.info("Message accepted: " + msg);

      if (holderCommAddress_ == null && reconfigureRequest_ != null) {
        outQueue_.add(new ReconfigureDHTAckMessage(reconfigureRequest_));
      }

      holderCommAddress_ = msg.getSourceAddress();
      logger_.info("Holder detected at " + holderCommAddress_);
      cleanCache();
      return null;
    }


    @Override
    public Void visit(PutDHTMessage msg) {
      put(msg, false, null);
      return null;
    }

    @Override
    public Void visit(GetDHTMessage msg) {
      get(msg, false, null);
      return null;
    }
  }
}
