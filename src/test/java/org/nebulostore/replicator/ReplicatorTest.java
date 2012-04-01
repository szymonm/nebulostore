package org.nebulostore.replicator;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.nebulostore.addressing.ObjectId;
import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.Message;
import org.nebulostore.replicator.messages.ConfirmationMessage;
import org.nebulostore.replicator.messages.GetObjectMessage;
import org.nebulostore.replicator.messages.ReplicatorErrorMessage;
import org.nebulostore.replicator.messages.SendObjectMessage;
import org.nebulostore.replicator.messages.StoreObjectMessage;

import static org.junit.Assert.assertTrue;

/**
 * @author szymonmatejczyk
 */
public class ReplicatorTest {
  private SimpleStringFile file1_ = new SimpleStringFile("Test string");
  private SimpleIntegerFile file2_ = new SimpleIntegerFile(273);

  private BlockingQueue<Message> inQueue_ = new LinkedBlockingQueue<Message>();
  private BlockingQueue<Message> networkQueue_ = new LinkedBlockingQueue<Message>();
  private Replicator replicator_ = new Replicator(null, inQueue_, null);

  private Thread replicatorThread_;

  @Before
  public void setUp() {
    DOMConfigurator.configure("resources/conf/log4j.xml");
  }

  /**
   * Saving file. Retrieving it using id.
   */
  @Test
  public void testStoreGetMessages() {
    ObjectId objectId1 = new ObjectId(new BigInteger("3"));
    byte[] enc = {33, 12};
    EncryptedEntity entity1 = new EncryptedEntity(enc);
    StoreObjectMessage storeMessage = new StoreObjectMessage("job1", null,
        null,
        objectId1, entity1);
    inQueue_.add(storeMessage);

    replicator_.setNetworkQueue(networkQueue_);

    replicatorThread_ = new Thread(replicator_);
    replicatorThread_.start();

    Message msg;
    try {
      Thread.sleep(300);
      msg = networkQueue_.take();
      if (!(msg instanceof ConfirmationMessage)) {
        assertTrue(false);
        return;
      }
    } catch (InterruptedException exception) {
      assertTrue(false);
      return;
    }

    GetObjectMessage getMessage = new GetObjectMessage(null, null, objectId1);
    inQueue_.add(getMessage);

    try {
      msg = networkQueue_.take();
    } catch (InterruptedException exception) {
      assertTrue(false);
      return;
    }

    if (msg instanceof SendObjectMessage) {
      SendObjectMessage som = (SendObjectMessage) msg;
      assertTrue(Arrays.equals(enc, som.encryptedEntity_.getEncryptedData()));
    } else if (msg instanceof ReplicatorErrorMessage) {
      ReplicatorErrorMessage rem = (ReplicatorErrorMessage) msg;
      assertTrue(rem.getMessage(), false);
    } else {
      assertTrue("Unknown message type received.", false);
      return;
    }
  }
}
