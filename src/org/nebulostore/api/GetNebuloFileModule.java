package org.nebulostore.api;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.DirectoryEntry;
import org.nebulostore.appcore.EncryptedEntity;
import org.nebulostore.appcore.EntryId;
import org.nebulostore.appcore.HardLink;
import org.nebulostore.appcore.InlineData;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.NebuloDir;
import org.nebulostore.appcore.NebuloFile;
import org.nebulostore.appcore.NebuloKey;
import org.nebulostore.appcore.NebuloObject;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.crypto.CryptoException;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.replicator.messages.GetObjectMessage;
import org.nebulostore.replicator.messages.SendObjectMessage;

/**
 * @author bolek
 * Job module that realizes getNebuloFile() API function.
 */
public class GetNebuloFileModule extends ApiModule<NebuloFile> {

  private NebuloKey nebuloKey_;
  private StateMachineVisitor visitor_;

  private static Logger logger_ = Logger.getLogger(GetNebuloFileModule.class);

  public GetNebuloFileModule(NebuloKey nebuloKey) {
    nebuloKey_ = nebuloKey;
    visitor_ = new StateMachineVisitor();
  }

  /*
   * Constructor that runs newly created module.
   */
  public GetNebuloFileModule(NebuloKey nebuloKey, BlockingQueue<Message> dispatcherQueue) {
    nebuloKey_ = nebuloKey;
    visitor_ = new StateMachineVisitor();
    runThroughDispatcher(dispatcherQueue);
  }

  /**
   * States of the state machine.
   */
  private enum STATE { INIT, DHT_QUERY, PATH_SEGMENT_QUERY, FILE_RECEIVED };

  /**
   * Visitor class that acts as a state machine realizing the procedure of resolving the path
   * and fetching the file.
   */
  private class StateMachineVisitor extends MessageVisitor<Void> {
    // TODO(bolek): Add descriptive names for states.
    private STATE state_;
    private int currentPathPart_;

    public StateMachineVisitor() {
      state_ = STATE.INIT;
      currentPathPart_ = 0;
    }

    public Void visit(JobInitMessage message) {
      if (state_ == STATE.INIT) {
        // State 1 - Send appKey to DHT and wait for reply.
        state_ = STATE.DHT_QUERY;
        jobId_ = message.getId();

        logger_.debug("Adding GetDHT to network queue (" + nebuloKey_.appKey_.appKey_ + ", " +
            jobId_ + ").");
        networkQueue_.add(new GetDHTMessage(jobId_, new KeyDHT(nebuloKey_.appKey_.appKey_)));
      } else {
        logger_.warn("JobInitMessage received in state " + state_.name());
      }
      return null;
    }

    public Void visit(ValueDHTMessage message) {
      if (state_ == STATE.DHT_QUERY) {

        // State 2 - Receive reply from DHT and iterate over logical path segments asking
        // for consecutive parts.
        state_ = STATE.PATH_SEGMENT_QUERY;
        // TODO(bolek): How to avoid casting here? Make ValueDHTMessage generic?
        DirectoryEntry entry = (DirectoryEntry) message.getValue().getValue();
        currentPathPart_ = 0;
        handleEntry(entry);
      } else {
        logger_.warn("ValueDHTMessage received in state " + state_.name());
      }
      return null;
    }

    public Void visit(SendObjectMessage message) {
      if (state_ == STATE.PATH_SEGMENT_QUERY) {
        NebuloObject nebuloObject;
        try {
          nebuloObject = CryptoUtils
              .decryptNebuloObject(message.encryptedEntity_);
        } catch (CryptoException exception) {
          // TODO(bolek): Error not fatal? Retry?
          endWithError(exception);
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
            endWithError(exception);
          }
        } else if (nebuloObject instanceof NebuloFile) {
          // State 3 - Finally got the file, return it;
          state_ = STATE.FILE_RECEIVED;
          endWithSuccess((NebuloFile) nebuloObject);
        } else {
          logger_.warn("Unrecognized subclass of NebuloObject.");
        }
      } else {
        logger_.warn("SendObjectMessage received in state " + state_);
      }
      return null;
    }

    // Performs some action depending on actual type of <entry>.
    // For HardLink or SoftLink - continue path resolution.
    // For InlineData - return data as NebuloFile.
    private void handleEntry(DirectoryEntry entry) {
      // TODO(bolek): visitor!
      if (entry instanceof InlineData) {
        endWithSuccess(new NebuloFile(((InlineData) entry).data_));
      } else if (entry instanceof HardLink) {
        // TODO(bolek): Ask other replicas if first query is unsuccessful.
        HardLink hardLink = (HardLink) entry;
        // Source address will be added by Network module.
        networkQueue_.add(new GetObjectMessage(jobId_, null, hardLink.objectPhysicalAddresses_[0],
            hardLink.objectId_));
      } else {
        logger_.warn("Unrecognized subclass of DirectoryEntry.");
      }
      // TODO(bolek): SoftLink not yet supported.
      logger_.error("SoftLink not yet supported.");
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    // Handling logic lies inside our visitor class.
    message.accept(visitor_);
  }
}
