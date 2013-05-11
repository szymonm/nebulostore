package org.nebulostore.replicator;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Ignore;
import org.junit.Test;
import org.nebulostore.appcore.addressing.ObjectId;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.model.EncryptedObject;
import org.nebulostore.replicator.messages.ConfirmationMessage;
import org.nebulostore.replicator.messages.GetObjectMessage;
import org.nebulostore.replicator.messages.QueryToStoreObjectMessage;
import org.nebulostore.replicator.messages.ReplicatorErrorMessage;
import org.nebulostore.replicator.messages.SendObjectMessage;
import org.nebulostore.replicator.messages.TransactionResultMessage;

import static org.junit.Assert.assertTrue;

/**
 * @author szymonmatejczyk
 */
public class ReplicatorTest {

  private final BlockingQueue<Message> inQueue_ = new LinkedBlockingQueue<Message>();
  private final BlockingQueue<Message> inQueue1_ = new LinkedBlockingQueue<Message>();
  private final BlockingQueue<Message> networkQueue_ = new LinkedBlockingQueue<Message>();
  private final BlockingQueue<Message> deadDispatcherQueue_ = new LinkedBlockingQueue<Message>();
  private final Replicator replicator_ = new Replicator(null, inQueue_, deadDispatcherQueue_);
  private final Replicator replicator1_ = new Replicator(null, inQueue1_, deadDispatcherQueue_);

  private Thread replicatorThread_;

  // TODO(bolek): Refactor this test with fake disk writes.
  /**
   * Saving file. Retrieving it using id.
   * @throws InterruptedException
   * @throws NebuloException
   */
  @Ignore
  @Test
  public void testStoreGetMessages() throws InterruptedException, NebuloException {
    ObjectId objectId1 = new ObjectId(new BigInteger("3"));
    byte[] enc = {33, 12};
    EncryptedObject entity1 = new EncryptedObject(enc);

    String[] previousVersion = {"version1"};
    QueryToStoreObjectMessage storeMessage = new QueryToStoreObjectMessage("job1", null,
        null, objectId1, entity1, new HashSet<String>(Arrays.asList(previousVersion)), "");
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

    TransactionResultMessage transactionResult = new TransactionResultMessage("job1", null, null,
        TransactionAnswer.COMMIT);
    inQueue_.add(transactionResult);

    // replicator thread died here
    Thread.sleep(100);

    replicatorThread_ = new Thread(replicator1_);
    replicator1_.setNetworkQueue(networkQueue_);
    replicatorThread_.start();

    GetObjectMessage getMessage = new GetObjectMessage(null, null, objectId1, "");
    inQueue1_.add(getMessage);

    try {
      msg = networkQueue_.take();
    } catch (InterruptedException exception) {
      assertTrue(false);
      return;
    }

    if (msg instanceof SendObjectMessage) {
      SendObjectMessage som = (SendObjectMessage) msg;
      assertTrue(Arrays.equals(enc, som.getEncryptedEntity().getEncryptedData()));
    } else if (msg instanceof ReplicatorErrorMessage) {
      ReplicatorErrorMessage rem = (ReplicatorErrorMessage) msg;
      assertTrue(rem.getMessage(), false);
    } else {
      assertTrue("Unknown message type received.", false);
      return;
    }
  }
}
