package org.nebulostore.communication.bdbdht;

import java.io.File;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.dht.ValueDHT;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Durability;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

/**
 * Implementation of berkely db based engine for...
 *
 * @author marcin
 */
public class BdbPeer extends Module {

  private static String configurationPath_ = "resources/conf/communication/BdbPeer.xml";
  private static Logger logger_ = Logger.getLogger(BdbPeer.class);

  private final String storagePath;
  private final String storeName;
  private final Database database;
  private final Environment env;

  public BdbPeer(BlockingQueue<Message> inQueue, BlockingQueue<Message> outQueue) {
    super(inQueue, outQueue);

    // TODO: move to factory in appcore
    XMLConfiguration config = null;
    try {
      config = new XMLConfiguration(configurationPath_);
    } catch (ConfigurationException cex) {
      logger_.error("Configuration read error in: " + configurationPath_);
    }
    storagePath = config.getString("sleepycat.storage-path",
        "/tmp/nebulostore-bdb");

    storeName = config.getString("sleepycat.store-name", "nebulostore-bdb");

    logger_.debug("storagePath: " + storagePath);

    env = createEnvironment(new File(storagePath));

    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setTransactional(true);
    dbConfig.setAllowCreate(true);

    database = env.openDatabase(null, storeName, dbConfig);

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

  @Override
  protected void processMessage(Message message) throws NebuloException {
    logger_.info("messge accepted.");

    if (message instanceof PutDHTMessage) {
      PutDHTMessage putMsg = (PutDHTMessage) message;

      String key = putMsg.getKey().toString();
      String value = putMsg.getValue().serializeValue();
      // TODO: Serialization error handling as well
      Transaction t = env.beginTransaction(null, null);
      database.put(t, new DatabaseEntry(key.getBytes()), new DatabaseEntry(
          value.getBytes()));

      t.commit();
      outQueue_.add(new OkDHTMessage());
    }

    if (message instanceof GetDHTMessage) {
      GetDHTMessage putMsg = (GetDHTMessage) message;

      KeyDHT key = putMsg.getKey();
      DatabaseEntry data = new DatabaseEntry();

      if (database.get(null, new DatabaseEntry(key.toString().getBytes()),
          data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
        ValueDHT value = ValueDHT.build(new String(data.getData()));
        outQueue_.add(new ValueDHTMessage(key, value));
      } else {
        // TODO: Error handling
      }

    }
  }
}
