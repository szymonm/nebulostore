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
import org.nebulostore.async.SendAsynchronousMessagesForPeerModule;
import org.nebulostore.async.messages.AsynchronousMessage;
import org.nebulostore.async.messages.DeleteNebuloObjectMessage;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.messages.ErrorCommMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.replicator.messages.ConfirmationMessage;
import org.nebulostore.replicator.messages.DeleteObjectMessage;
import org.nebulostore.timer.TimerContext;

/**
 * @author bolek
 */
public class DeleteNebuloObjectModule extends ReturningJobModule<Void> {

  private static Logger logger_ = Logger.getLogger(DeleteNebuloObjectModule.class);

  /* number of confirmation messages required from replicas to return success */
  // private static final int CONFIRMATIONS_REQUIRED = 2;
  private static final Long TIMEOUT_MILLIS = 5000L;

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
        logger_.warn("JobInitMessage received in state " + state_.name());
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
        }

        for (CommAddress replicator : group) {
          String remoteJobId = CryptoUtils.getRandomId().toString();
          networkQueue_.add(new DeleteObjectMessage(remoteJobId , null, replicator,
                address_.getObjectId(), getJobId()));
          recipientsSet_.add(replicator);
        }
        TimerContext.getInstance().addDelayedMessage(System.currentTimeMillis() + TIMEOUT_MILLIS,
            new DeleteTimeoutMessage(jobId_));
      } else {
        logger_.warn("ValueDHTMessage received in state " + state_.name());
      }
      return null;
    }

    @Override
    public Void visit(ConfirmationMessage message) {
      if (state_ == STATE.REPLICA_UPDATE) {
        recipientsSet_.remove(message.getSourceAddress());
        if (recipientsSet_.isEmpty()) {
          // All recipients replied. From now we can ignore DeleteTimeoutMessage and finish module.
          // TODO(bolek): delete timer message?
          state_ = STATE.DONE;
          finishModule();
        }
      } else {
        logger_.warn("ConfirmationMessage received in state " + state_);
      }
      return null;
    }

    @Override
    public Void visit(DeleteTimeoutMessage message) {
      if (state_ == STATE.REPLICA_UPDATE) {
        // TODO(bolek): should we count confirmations and fail if received too few?
        //   Does failing in case of delete() make sense?
        finishModule();
      } else {
        logger_.warn("ConfirmationMessage received in state " + state_);
      }
      return null;
    }

    @Override
    public Void visit(ErrorCommMessage message) {
      logger_.warn("ErrorCommMessage received in state " + state_);
      // TODO(bolek): Can we safely ignore it here and just wait for timeout and async messages?
      return null;
    }

    private void finishModule() {
      // Send AMs to modules that did not respond and return.
      for (CommAddress deadReplicator : recipientsSet_) {
        AsynchronousMessage asynchronousMessage = new DeleteNebuloObjectMessage(address_);
        new SendAsynchronousMessagesForPeerModule(deadReplicator, asynchronousMessage, outQueue_);
      }
      endWithSuccess(null);
    }

  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    // Handling logic lies inside our visitor class.
    message.accept(visitor_);
  }

  /**
   * Message used for timeout in delete() API call.
   * @author bolek
   */
  public class DeleteTimeoutMessage extends Message {
    private static final long serialVersionUID = -1273528925587802574L;

    public DeleteTimeoutMessage(String jobID) {
      super(jobID);
    }

    public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
      return visitor.visit(this);
    }
  }

}
