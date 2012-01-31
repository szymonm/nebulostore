package org.nebulostore.api;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.DirectoryEntry;
import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.EntryId;
import org.nebulostore.appcore.HardLink;
import org.nebulostore.appcore.InlineData;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.NebuloDir;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.NebuloKey;
import org.nebulostore.appcore.NebuloObject;
import org.nebulostore.appcore.exceptions.KillModuleException;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.crypto.CryptoException;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.replicator.messages.GetObjectMessage;
import org.nebulostore.replicator.messages.SendObjectMessage;

/**
 * @author bolek
 * Job module that realizes getNebuloFile() API function.
 */
public class GetNebuloFileModule extends JobModule {

  private BlockingQueue<ApiMessage> resultQueue_;
  private MessageVisitor<Void> visitor_;
  private NebuloKey nebuloKey_;

  private static Logger logger_ = Logger.getLogger(GetNebuloFileModule.class);

  private void log(String message) {
    logger_.warn(message);
  }

  private void log(String className, int badState) {
    log(className + " received in state " + String.valueOf(badState) + ".");
  }

  // TODO(bolek): Result queue is used only once, maybe use sth simpler than queue here?
  public GetNebuloFileModule(NebuloKey nebuloKey, BlockingQueue<ApiMessage> resultQueue) {
    visitor_ = new GetObjectModuleVisitor();
    resultQueue_ = resultQueue;
    nebuloKey_ = nebuloKey;
  }

  /**
   * Visitor class that acts as a state machine realizing the procedure of resolving the path
   * and fetching the file.
   */
  private class GetObjectModuleVisitor extends MessageVisitor<Void> {
    // TODO(bolek): Add descriptive names for states.
    private int state_;
    private int currentPathPart_;

    public GetObjectModuleVisitor() {
      state_ = 0;
      currentPathPart_ = 0;
    }

    public Void visit(JobInitMessage message) {
      if (state_ == 0) {
        // State 1 - Send appKey to DHT and wait for reply.
        state_ = 1;
        taskId_ = message.getId();

        logger_.debug("Adding GetDHT to network queue (" + nebuloKey_.appKey_.appKey_ + ", " +
            taskId_ + ").");
        networkQueue_.add(new GetDHTMessage(taskId_, new KeyDHT(nebuloKey_.appKey_.appKey_)));
      } else {
        log("JobInitMessage", state_);
      }
      return null;
    }

    public Void visit(ValueDHTMessage message) throws KillModuleException {
      if (state_ == 1) {

        // State 2 - Receive reply from DHT and iterate over logical path segments asking
        // for consecutive parts.
        state_ = 2;
        // TODO(bolek): How to avoid casting here? Make ValueDHTMessage generic?
        DirectoryEntry entry = (DirectoryEntry) message.getValue().getValue();
        currentPathPart_ = 0;
        handleEntry(entry);
      } else {
        log("ValueDHTMessage", state_);
      }
      return null;
    }

    public Void visit(SendObjectMessage message) throws KillModuleException {
      if (state_ == 2) {
        NebuloObject nebuloObject;
        try {
          nebuloObject = CryptoUtils
              .decryptNebuloObject(message.encryptedEntity_);
        } catch (CryptoException exception) {
          // TODO(bolek): Error not fatal? Retry?
          dieWithError(exception.getMessage());
          return null;
        }
        // TODO(bolek): visitor!
        if (nebuloObject instanceof NebuloDir) {
          NebuloDir dir = (NebuloDir) nebuloObject;
          EntryId entryId = nebuloKey_.path_.get(currentPathPart_).entryId_;
          EncryptedEntity encryptedEntry = dir.getEntries().get(entryId);
          DirectoryEntry entry;
          try {
            entry = CryptoUtils.decryptDirectoryEntry(encryptedEntry);
            currentPathPart_++;
            handleEntry(entry);
          } catch (CryptoException exception) {
            dieWithError(exception.getMessage());
          }
        } else if (nebuloObject instanceof NebuloFile) {
          // State 3 - Finally got the file, return it;
          state_ = 3;
          dieWithSuccess((NebuloFile) nebuloObject);
        } else {
          log("Unrecognized subclass of NebuloObject.");
        }
      } else {
        log("SendObjectMessage", state_);
      }
      return null;
    }

    private void handleEntry(DirectoryEntry entry) throws KillModuleException {
      // TODO(bolek): visitor!
      if (entry instanceof InlineData) {
        dieWithSuccess(new NebuloFile(((InlineData) entry).data_));
      } else if (entry instanceof HardLink) {
        // TODO(bolek): Ask other replicas if first query is unsuccessful.
        HardLink hardLink = (HardLink) entry;
        // Source address will be added by Network module.
        networkQueue_.add(new GetObjectMessage(taskId_, null, hardLink.objectPhysicalAddresses_[0],
            hardLink.objectId_));
      } else {
        log("Unrecognized subclass of DirectoryEntry.");
      }
      // TODO(bolek): SoftLink not yet supported.
    }

    private void dieWithSuccess(NebuloFile nebuloFile) throws KillModuleException {
      // Return the result.
      resultQueue_.add(new ApiGetNebuloFileMessage(nebuloFile));
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
