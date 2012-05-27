package org.nebulostore.api;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.ContractList;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.addressing.ReplicationGroup;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.Metadata;
import org.nebulostore.appcore.NebuloObject;
import org.nebulostore.appcore.ReturningJobModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.crypto.CryptoException;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.replicator.messages.ConfirmationMessage;
import org.nebulostore.replicator.messages.ReplicatorErrorMessage;
import org.nebulostore.replicator.messages.StoreObjectMessage;

/**
 * @author bolek
 */
public class WriteNebuloObjectModule extends ReturningJobModule<Void> {

  private final NebuloAddress address_;
  private final NebuloObject object_;
  private final StateMachineVisitor visitor_;

  private static Logger logger_ = Logger.getLogger(WriteNebuloObjectModule.class);

  /*
   * Constructor that runs newly created module.
   */
  public WriteNebuloObjectModule(NebuloAddress nebuloKey, NebuloObject object,
      BlockingQueue<Message> dispatcherQueue) {
    address_ = nebuloKey;
    object_ = object;
    visitor_ = new StateMachineVisitor();
    runThroughDispatcher(dispatcherQueue);
  }

  /**
   * States of the state machine.
   */
  private enum STATE { INIT, DHT_QUERY, REPLICA_UPDATE, DONE };

  /**
   * Visitor class that acts as a state machine realizing the procedure of fetching the file.
   */
  private class StateMachineVisitor extends MessageVisitor<Void> {
    private STATE state_;

    public StateMachineVisitor() {
      state_ = STATE.INIT;
    }

    @Override
    public Void visit(JobInitMessage message) {
      if (state_ == STATE.INIT) {
        // State 1 - Send groupId to DHT and wait for reply.
        state_ = STATE.DHT_QUERY;
        jobId_ = message.getId();

        logger_.debug("Adding GetDHT to network queue (" + address_.getAppKey() + ", " +
            jobId_ + ").");
        networkQueue_.add(new GetDHTMessage(jobId_, new KeyDHT(address_.getAppKey().getKey())));
      } else {
        logger_.warn("JobInitMessage received in state " + state_.name());
      }
      return null;
    }

    @Override
    public Void visit(ValueDHTMessage message) {
      logger_.debug("Got ValueDHTMessage " + message.toString());
      if (state_ == STATE.DHT_QUERY) {

        // State 2 - Receive reply from DHT and iterate over logical path segments asking
        // for consecutive parts.
        state_ = STATE.REPLICA_UPDATE;
        // TODO(bolek): How to avoid casting here? Make ValueDHTMessage generic?
        // TODO(bolek): Merge this with similar part from GetNebuloFileModule?
        Metadata metadata = (Metadata) message.getValue().getValue();
        logger_.debug("Metadata: " + metadata);

        ContractList contractList = metadata.getContractList();
        logger_.debug("ContractList: " + contractList);
        ReplicationGroup group = contractList.getGroup(address_.getObjectId());
        logger_.debug("Group: " + group);
        if (group == null) {
          endWithError(new NebuloException("No peers replicating this object."));
        }
        // TODO(bolek): Ask other replicas if first query is unsuccessful.
        // Source address will be added by Network module.
        try {
          logger_.info("Value DHT Message received. Sending StoreObjectMessage to: " +
            group.getReplicator(0));
          networkQueue_.add(new StoreObjectMessage(CryptoUtils.getRandomId().toString(),
              null, group.getReplicator(0), address_.getObjectId(),
              CryptoUtils.encryptObject(object_), jobId_));
        } catch (CryptoException exception) {
          endWithError(new NebuloException("Unable to encrypt object.", exception));
        }
      } else {
        logger_.warn("ValueDHTMessage received in state " + state_.name());
      }
      return null;
    }

    @Override
    public Void visit(ConfirmationMessage message) {
      if (state_ == STATE.REPLICA_UPDATE) {
        state_ = STATE.DONE;
        endWithSuccess(null);
      } else {
        logger_.warn("SendObjectMessage received in state " + state_);
      }
      return null;
    }

    @Override
    public Void visit(ReplicatorErrorMessage message) {
      if (state_ == STATE.REPLICA_UPDATE) {
        // TODO(bolek): ReplicatorErrorMessage should contain exception instead of string.
        endWithError(new NebuloException(message.getMessage()));
      } else {
        logger_.warn("SendObjectMessage received in state " + state_);
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
