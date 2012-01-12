package tests.org.nebulostore.api;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.junit.Test;
import org.nebulostore.api.ApiGetNebuloFileMessage;
import org.nebulostore.api.ApiMessage;
import org.nebulostore.api.GetNebuloFileModule;
import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.EntryId;
import org.nebulostore.appcore.HardLink;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.NebuloDir;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.NebuloKey;
import org.nebulostore.appcore.ObjectId;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.ValueDHT;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.crypto.CryptoException;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.replicator.messages.GetObjectMessage;
import org.nebulostore.replicator.messages.SendObjectMessage;

import tests.org.nebulostore.TestUtils;

import static org.junit.Assert.assertTrue;

/**
 * getNebuloFile() API call test class.
 */
public class GetNebuloFileModuleTest {

  private BlockingQueue<Message> inQueue_;
  private BlockingQueue<Message> outQueue_;
  private BlockingQueue<Message> networkQueue_;
  private BlockingQueue<ApiMessage> resultQueue_;
  private Thread thread_;

  @Before
  public void setUp() {
    inQueue_ = new LinkedBlockingQueue<Message>();
    outQueue_ = new LinkedBlockingQueue<Message>();
    resultQueue_ = new LinkedBlockingQueue<ApiMessage>();
    networkQueue_ = new LinkedBlockingQueue<Message>();
  }

  @Test
  public void testSuccessfulCall() {
    String[] dirIds   = {"dir_id_1"};
    String[] entryIds = {"entry_1"};
    NebuloKey nebuloKey = TestUtils.createNebuloKey("app_key", dirIds, entryIds, "objectID");
    assertTrue(nebuloKey.appKey_.appKey_.equals("app_key"));

    GetNebuloFileModule module = new GetNebuloFileModule(nebuloKey, resultQueue_);
    module.setInQueue(inQueue_);
    module.setOutQueue(outQueue_);
    module.setNetworkQueue(networkQueue_);
    thread_ = new Thread(module);
    thread_.start();

    byte[] targetObject = {4, 13};

    // Init message.
    inQueue_.add(new JobInitMessage("job_id", null));

    // Wait for DHT query.
    Message msg;
    try {
      msg = networkQueue_.take();
    } catch (InterruptedException e) {
      assertTrue(false);
      return;
    }
    GetDHTMessage dhtMessage;
    assertTrue(msg instanceof GetDHTMessage);
    dhtMessage = (GetDHTMessage) msg;

    // Verify DHT query.
    assertTrue(dhtMessage.getKey().toString().equals("app_key"));

    // Send DHT reply with dir_id_1 addresses.
    CommAddress[] tab1 = {null};
    HardLink hardLink1 = new HardLink(new ObjectId("dir_id_1"), tab1);
    inQueue_.add(new ValueDHTMessage(dhtMessage, dhtMessage.getKey(), new ValueDHT(hardLink1)));

    // Wait for dir_id_1 query via SendObjectMessage.
    try {
      msg = networkQueue_.take();
    } catch (InterruptedException e) {
      assertTrue(false);
      return;
    }
    GetObjectMessage getObjectMessage;
    assertTrue(msg instanceof GetObjectMessage);
    getObjectMessage = (GetObjectMessage) msg;
    assertTrue(getObjectMessage.objectId_.getKey().equals("dir_id_1"));

    // Send directory 1.
    HardLink entry = new HardLink(new ObjectId("objectID"), tab1);
    Map<EntryId, EncryptedEntity> entries = new TreeMap<EntryId, EncryptedEntity>();
    try {
      entries.put(new EntryId("entry_1"), CryptoUtils.encryptDirectoryEntry(entry));
    } catch (CryptoException e) {
      assertTrue(false);
    }
    NebuloDir dir = new NebuloDir(entries);
    try {
      inQueue_.add(new SendObjectMessage(null, null, CryptoUtils.encryptNebuloObject(dir)));
    } catch (CryptoException e) {
      System.out.print(e.getMessage());
      assertTrue(false);
    }

    // Wait for object query.
    try {
      msg = networkQueue_.take();
    } catch (InterruptedException e) {
      assertTrue(false);
      return;
    }
    assertTrue(msg instanceof GetObjectMessage);
    getObjectMessage = (GetObjectMessage) msg;
    assertTrue(getObjectMessage.objectId_.getKey().equals("objectID"));

    // Send targetObject.
    NebuloFile file = new NebuloFile(targetObject);
    try {
      inQueue_.add(new SendObjectMessage(null, null, CryptoUtils.encryptNebuloObject(file)));
    } catch (CryptoException e) {
      System.out.print(e.getMessage());
      assertTrue(false);
    }

    // Wait for API call result.
    ApiMessage apiMsg;
    try {
      apiMsg = resultQueue_.take();
    } catch (InterruptedException e) {
      assertTrue(false);
      return;
    }
    ApiGetNebuloFileMessage retMessage;
    assertTrue(apiMsg instanceof ApiGetNebuloFileMessage);
    retMessage = (ApiGetNebuloFileMessage) apiMsg;
    assertTrue(retMessage.getNebuloFile().data_.length == 2);
    assertTrue(retMessage.getNebuloFile().data_[0] == 4);
    assertTrue(retMessage.getNebuloFile().data_[1] == 13);

    // Wait for JobEndedMessage.
    Message endMsg;
    try {
      endMsg = outQueue_.take();
    } catch (InterruptedException e) {
      assertTrue(false);
      return;
    }
    assertTrue(endMsg instanceof JobEndedMessage);
    JobEndedMessage jeMsg = (JobEndedMessage) endMsg;
    assertTrue(jeMsg.getId().equals("job_id"));

    // Wait for thread to finish.
    try {
      thread_.join();
    } catch (InterruptedException e) {
      assertTrue(false);
    }
  }
}