package org.nebulostore.api;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.AppKey;
import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.EntryId;
import org.nebulostore.appcore.HardLink;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.NebuloDir;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.ObjectId;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.dht.ValueDHT;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.replicator.Replicator;

/**
 * @author bolek
 * Job module that realizes putKey() API function.
 */
public class PutKeyModule extends ApiModule<ObjectId> {

  private AppKey appKey_;
  private StateMachineVisitor visitor_;

  private static Logger logger_ = Logger.getLogger(PutKeyModule.class);

  public PutKeyModule(AppKey appKey) {
    appKey_ = appKey;
    visitor_ = new StateMachineVisitor();
  }

  /*
   * Constructor that runs newly created module.
   */
  public PutKeyModule(AppKey appKey, BlockingQueue<Message> dispatcherQueue) {
    appKey_ = appKey;
    visitor_ = new StateMachineVisitor();
    runThroughDispatcher(dispatcherQueue);
  }

  /**
   * States of the state machine.
   */
  private enum STATE { INIT, DHT_INSERT };

  /**
   * Visitor class that acts as a state machine realizing the procedure of creating new top-level
   * directory for a user.
   */
  private class StateMachineVisitor extends MessageVisitor<Void> {
    private STATE state_;
    private ObjectId dirId_;

    public StateMachineVisitor() {
      state_ = STATE.INIT;
    }

    public Void visit(JobInitMessage message) {
      if (state_ == STATE.INIT) {
        // State 1 - Send appKey to DHT and wait for reply.
        state_ = STATE.DHT_INSERT;
        jobId_ = message.getId();

        /*
         * THIS IS ONLY FOR TESTING PURPOSES AND WILL BE GONE WHEN OTHER API METHODS ARE IMPLEMENTED
         *
         * Create top-level directory with one file inside it. Store both objects in my replicator.
         * Create a mapping in DHT form my AppKey into a list of replicas containing only
         * my address.
         */
        CommAddress myAddr = CommunicationPeer.getPeerAddress();

        // File 'file1'.
        ObjectId fileId = new ObjectId("fileId");
        NebuloFile file1 = new NebuloFile("test file".getBytes());
        try {
          EncryptedEntity encryptedFile = CryptoUtils.encryptNebuloObject(file1);
          Replicator.storeObject(fileId, encryptedFile);
        } catch (NebuloException exception) {
          endWithError(new NebuloException("Error while creating sample file", exception));
        }

        // Dir 'topdir' with one entry "file1" -> fileId.
        ObjectId dirId = new ObjectId("topdir");
        HardLink fileLink = new HardLink("title?", fileId, new CommAddress[]{myAddr});
        try {
          EncryptedEntity encryptedLink = CryptoUtils.encryptDirectoryEntry(fileLink);
          Map<EntryId, EncryptedEntity> entries = new TreeMap<EntryId, EncryptedEntity>();
          entries.put(new EntryId("file1"), encryptedLink);
          NebuloDir topdir = new NebuloDir(entries);
          EncryptedEntity encryptedDir = CryptoUtils.encryptNebuloObject(topdir);
          Replicator.storeObject(dirId, encryptedDir);
        } catch (NebuloException exception) {
          endWithError(new NebuloException("Error while creating top-level directory", exception));
        }
        /*
         * END OF TEST
         */

        // List of top-dir replicators stored in DHT.
        // TODO(bolek): is it always a new dir? should addresses be taken from broker at this point?
        HardLink dhtValue = new HardLink("title?", dirId, new CommAddress[]{myAddr});
        dirId_ = dirId;
        networkQueue_.add(new PutDHTMessage(jobId_, new KeyDHT(appKey_.appKey_),
            new ValueDHT(dhtValue)));
      } else {
        logger_.warn("JobInitMessage received in state " + state_);
      }
      return null;
    }

    public Void visit(OkDHTMessage message) {
      if (state_ == STATE.DHT_INSERT) {
        endWithSuccess(dirId_);
      } else {
        logger_.warn("OkDHTMessage received in state " + state_);
      }
      return null;
    }

    public Void visit(ErrorDHTMessage message) {
      if (state_ == STATE.DHT_INSERT) {
        endWithError(new NebuloException("DHT write returned with error", message.getException()));
      } else {
        logger_.warn("ErrorDHTMessage received in state " + state_);
      }
      return null;
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    // Handling logic lies inside our visitor class.
    message.accept(visitor_);
  }
}
