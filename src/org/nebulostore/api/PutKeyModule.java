package org.nebulostore.api;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.AppKey;
import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.EntryId;
import org.nebulostore.appcore.HardLink;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.NebuloDir;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.ObjectId;
import org.nebulostore.appcore.exceptions.KillModuleException;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.dht.ValueDHT;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.replicator.Replicator;

/**
 * @author bolek
 * Job module that realizes putKey() API function.
 */
public class PutKeyModule extends JobModule {

  private BlockingQueue<ApiMessage> resultQueue_;
  private MessageVisitor<Void> visitor_;
  private AppKey appKey_;

  private static Logger logger_ = Logger.getLogger(PutKeyModule.class);

  private void log(String message) {
    logger_.warn(message);
  }

  private void log(String className, int badState) {
    log(className + " received in state " + String.valueOf(badState) + ".");
  }

  // TODO(bolek): Result queue is used only once, maybe use sth simpler than queue here?
  public PutKeyModule(AppKey appKey, BlockingQueue<ApiMessage> resultQueue) {
    visitor_ = new PutKeyModuleVisitor();
    resultQueue_ = resultQueue;
    appKey_ = appKey;
  }

  /**
   * Visitor class that acts as a state machine realizing the procedure of creating new top-level
   * directory for a user.
   */
  private class PutKeyModuleVisitor extends MessageVisitor<Void> {
    private int state_;
    private ObjectId dirId_;

    public PutKeyModuleVisitor() {
      state_ = 0;
    }

    public Void visit(JobInitMessage message) throws KillModuleException {
      if (state_ == 0) {
        // State 1 - Send appKey to DHT and wait for reply.
        state_ = 1;
        taskId_ = message.getId();

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
        } catch (NebuloException e) {
          dieWithError("Error while creating sample file");
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
        } catch (NebuloException e) {
          dieWithError("Error while creating top-level directory");
        }
        /*
         * END OF TEST
         */

        // List of top-dir replicators stored in DHT.
        // TODO(bolek): is it always a new dir? should addresses be taken from broker at this point?
        HardLink dhtValue = new HardLink("title?", dirId, new CommAddress[]{myAddr});
        dirId_ = dirId;
        networkQueue_.add(new PutDHTMessage(taskId_, new KeyDHT(appKey_.appKey_),
            new ValueDHT(dhtValue)));
      } else {
        log("JobInitMessage", state_);
      }
      return null;
    }

    public Void visit(OkDHTMessage message) throws KillModuleException {
      if (state_ == 1) {
        dieWithSuccess(dirId_);
      } else {
        log("OkDHTMessage", state_);
      }
      return null;
    }

    public Void visit(ErrorDHTMessage message) throws KillModuleException {
      if (state_ == 1) {
        dieWithError("DHT write gave error: " + message.getException().getMessage());
      } else {
        log("ErrorDHTMessage", state_);
      }
      return null;
    }

    private void dieWithSuccess(ObjectId dirId) throws KillModuleException {
      // Return the result.
      resultQueue_.add(new ApiPutKeyMessage(dirId));
      die();
    }

    private void dieWithError(String errorMessage) throws KillModuleException {
      // Inform user about the error.
      resultQueue_.add(new ApiErrorMessage(errorMessage));
      die();
    }

    private void die() throws KillModuleException {
      // Inform dispatcher that we are going to die.
      outQueue_.add(new JobEndedMessage(taskId_));
      // Tell run() main loop to stop.
      throw new KillModuleException();
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    // Handling logic lies inside our visitor class.
    message.accept(visitor_);
  }
}
