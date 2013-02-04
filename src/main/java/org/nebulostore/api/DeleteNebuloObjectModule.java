package org.nebulostore.api;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.ContractList;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.addressing.ReplicationGroup;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.Metadata;
import org.nebulostore.appcore.ReturningJobModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.messages.ErrorCommMessage;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.replicator.messages.ConfirmationMessage;
import org.nebulostore.replicator.messages.DeleteObjectMessage;

/**
 * @author bolek
 */
public class DeleteNebuloObjectModule extends ReturningJobModule<Void> {
  private static Logger logger_ = Logger.getLogger(DeleteNebuloObjectModule.class);

  private final NebuloAddress address_;
  private final StateMachineVisitor visitor_;

  /*
   * Constructor that runs newly created module.
   */
  public DeleteNebuloObjectModule(NebuloAddress nebuloKey, BlockingQueue<Message> dispatcherQueue) {
    address_ = nebuloKey;
    visitor_ = new StateMachineVisitor();
    setOutQueue(dispatcherQueue);
    runThroughDispatcher(dispatcherQueue);
  }

  /**
   * States of the state machine.
   */
  private enum STATE { INIT, DHT_QUERY, REPLICA_UPDATE, DONE };

  /**
   * Visitor class that acts as a state machine realizing the procedure of deleting the file.
   */
  private class StateMachineVisitor extends MessageVisitor<Void> {
    private final Set<CommAddress> recipientsSet_;
    // private int confirmations_;
    private STATE state_;

    public StateMachineVisitor() {
      recipientsSet_ = new HashSet<CommAddress>();
      // confirmations_ = 0;
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
        incorrectState(state_.name(), message);
      }
      return null;
    }

    @Override
    public Void visit(ValueDHTMessage message) {
      logger_.debug("Got ValueDHTMessage " + message.toString());
      if (state_ == STATE.DHT_QUERY) {
        state_ = STATE.REPLICA_UPDATE;
        // TODO(bolek): How to avoid casting here? Make ValueDHTMessage generic?
        // TODO(bolek): Merge this with similar part from GetNebuloFileModule?
        Metadata metadata = (Metadata) message.getValue().getValue();
        logger_.debug("Metadata: " + metadata);

        ContractList contractList = metadata.getContractList();
        logger_.debug("ContractList: " + contractList);
        ReplicationGroup group = contractList.getGroup(address_.getObjectId());
        logger_.debug("Replication group: " + group);
        if (group == null) {
          endWithError(new NebuloException("No peers replicating this object."));
        } else {
          for (CommAddress replicator : group) {
            String remoteJobId = CryptoUtils.getRandomId().toString();
            networkQueue_.add(new DeleteObjectMessage(remoteJobId , null, replicator,
                address_.getObjectId(), getJobId()));
            recipientsSet_.add(replicator);
          }
        }
      } else {
        incorrectState(state_.name(), message);
      }
      return null;
    }

    @Override
    public Void visit(ErrorDHTMessage message) {
      if (state_ == STATE.DHT_QUERY) {
        logger_.debug("Received ErrorDHTMessage");
        endWithError(new NebuloException("Could not fetch metadata from DHT.",
            message.getException()));
      } else {
        incorrectState(state_.name(), message);
      }
      return null;
    }

    @Override
    public Void visit(ConfirmationMessage message) {
      if (state_ == STATE.REPLICA_UPDATE) {
        logger_.debug("Confirmation message, removing: " + message.getSourceAddress());
        recipientsSet_.remove(message.getSourceAddress());
        if (recipientsSet_.isEmpty()) {
          logger_.debug("All recipients have replied. Finishing.");
          // All recipients replied. From now we can ignore DeleteTimeoutMessage and finish module.
          // TODO(bolek): delete timer message?
          state_ = STATE.DONE;
          endWithSuccess(null);
        }
      } else {
        incorrectState(state_.name(), message);
      }
      return null;
    }

    @Override
    public Void visit(ErrorCommMessage message) {
      incorrectState(state_.name(), message);
      // TODO(bolek): Can we safely ignore it here and just wait for timeout and async messages?
      return null;
    }

    // TODO(bolek): Maybe move it to a new superclass StateMachine?
    private void incorrectState(String stateName, Message message) {
      logger_.warn(message.getClass().getName() + " received in state " + stateName);
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    // Handling logic lies inside our visitor class.
    message.accept(visitor_);
  }
}
