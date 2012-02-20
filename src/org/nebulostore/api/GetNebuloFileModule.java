package org.nebulostore.api;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.ContractList;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.addressing.ReplicationGroup;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
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
public class GetNebuloFileModule extends ApiModule<NebuloObject> {

  private NebuloAddress address_;
  private StateMachineVisitor visitor_;

  private static Logger logger_ = Logger.getLogger(GetNebuloFileModule.class);

  public GetNebuloFileModule(NebuloAddress nebuloKey) {
    address_ = nebuloKey;
    visitor_ = new StateMachineVisitor();
  }

  /*
   * Constructor that runs newly created module.
   */
  public GetNebuloFileModule(NebuloAddress nebuloKey, BlockingQueue<Message> dispatcherQueue) {
    address_ = nebuloKey;
    visitor_ = new StateMachineVisitor();
    runThroughDispatcher(dispatcherQueue);
  }

  /**
   * States of the state machine.
   */
  private enum STATE { INIT, DHT_QUERY, REPLICA_FETCH, FILE_RECEIVED };

  /**
   * Visitor class that acts as a state machine realizing the procedure of fetching the file.
   */
  private class StateMachineVisitor extends MessageVisitor<Void> {
    private STATE state_;
    private int currentPathPart_;

    public StateMachineVisitor() {
      state_ = STATE.INIT;
      currentPathPart_ = 0;
    }

    public Void visit(JobInitMessage message) {
      if (state_ == STATE.INIT) {
        // State 1 - Send groupId to DHT and wait for reply.
        state_ = STATE.DHT_QUERY;
        jobId_ = message.getId();

        logger_.debug("Adding GetDHT to network queue (" + address_.getGroupId() + ", " +
            jobId_ + ").");
        networkQueue_.add(new GetDHTMessage(jobId_, new KeyDHT(address_.getGroupId().getKey())));
      } else {
        logger_.warn("JobInitMessage received in state " + state_.name());
      }
      return null;
    }

    public Void visit(ValueDHTMessage message) {
      if (state_ == STATE.DHT_QUERY) {

        // State 2 - Receive reply from DHT and iterate over logical path segments asking
        // for consecutive parts.
        state_ = STATE.REPLICA_FETCH;
        // TODO(bolek): How to avoid casting here? Make ValueDHTMessage generic?
        ContractList contractList = (ContractList) message.getValue().getValue();
        ReplicationGroup group = contractList.getGroup(address_.getObjectId());
        if (group == null) {
          endWithError(new NebuloException("No peers replicating this object."));
        }
        // TODO(bolek): Ask other replicas if first query is unsuccessful.
        // Source address will be added by Network module.
        networkQueue_.add(new GetObjectMessage(jobId_, null, group.getReplicator(0),
            address_.getObjectId()));
      } else {
        logger_.warn("ValueDHTMessage received in state " + state_.name());
      }
      return null;
    }

    public Void visit(SendObjectMessage message) {
      if (state_ == STATE.REPLICA_FETCH) {
        NebuloObject nebuloObject;
        try {
          nebuloObject = CryptoUtils.decryptNebuloObject(message.encryptedEntity_);
        } catch (CryptoException exception) {
          // TODO(bolek): Error not fatal? Retry?
          endWithError(exception);
          return null;
        }
        // State 3 - Finally got the file, return it;
        state_ = STATE.FILE_RECEIVED;
        endWithSuccess(nebuloObject);
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
