package org.nebulostore.api;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.junit.Test;
import org.nebulostore.appcore.Message;



/**
 * getNebuloFile() API call test class.
 */
public class GetNebuloFileModuleTest {

  private BlockingQueue<Message> inQueue_;
  private BlockingQueue<Message> outQueue_;
  private BlockingQueue<Message> networkQueue_;
  private Thread thread_;

  @Before
  public void setUp() {
    inQueue_ = new LinkedBlockingQueue<Message>();
    outQueue_ = new LinkedBlockingQueue<Message>();
    networkQueue_ = new LinkedBlockingQueue<Message>();
  }

  @Test
  public void testSuccessfulCall() {
    /*String[] dirIds   = {"dir_id_1"};
    String[] entryIds = {"entry_1"};
    NebuloKey nebuloKey = TestUtils.createNebuloKey("app_key", dirIds, entryIds, "objectID");
    assertTrue(nebuloKey.appKey_.appKey_.equals("app_key"));

    GetNebuloFileModule module = new GetNebuloFileModule(nebuloKey);
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
    } catch (InterruptedException exception) {
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
    } catch (InterruptedException exception) {
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
    } catch (CryptoException exception) {
      assertTrue(false);
    }
    NebuloDir dir = new NebuloDir(entries);
    try {
      inQueue_.add(new SendObjectMessage(null, null, CryptoUtils.encryptNebuloObject(dir)));
    } catch (CryptoException exception) {
      System.out.print(exception.getMessage());
      assertTrue(false);
    }

    // Wait for object query.
    try {
      msg = networkQueue_.take();
    } catch (InterruptedException exception) {
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
    } catch (CryptoException exception) {
      System.out.print(exception.getMessage());
      assertTrue(false);
    }

    // Wait for API call result.
    NebuloFile nebuloFile = null;
    try {
      nebuloFile = module.getResult(5);
    } catch (NebuloException exception) {
      System.out.print(exception.getMessage());
      assertTrue(false);
    }
    assertTrue(nebuloFile.data_.length == 2);
    assertTrue(nebuloFile.data_[0] == 4);
    assertTrue(nebuloFile.data_[1] == 13);

    // Wait for JobEndedMessage.
    Message endMsg;
    try {
      endMsg = outQueue_.take();
    } catch (InterruptedException exception) {
      assertTrue(false);
      return;
    }
    assertTrue(endMsg instanceof JobEndedMessage);
    JobEndedMessage jeMsg = (JobEndedMessage) endMsg;
    assertTrue(jeMsg.getId().equals("job_id"));

    // Wait for thread to finish.
    try {
      thread_.join();
    } catch (InterruptedException exception) {
      assertTrue(false);
    }*/
  }
}
