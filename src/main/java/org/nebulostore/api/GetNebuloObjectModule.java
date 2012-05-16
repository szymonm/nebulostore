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
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.crypto.CryptoException;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.replicator.messages.GetObjectMessage;
import org.nebulostore.replicator.messages.ReplicatorErrorMessage;
import org.nebulostore.replicator.messages.SendObjectMessage;

/**
 * @author bolek
 * Job module that fetches an existing object from NebuloStore.
 */
public class GetNebuloObjectModule extends ReturningJobModule<NebuloObject> {

  private final NebuloAddress address_;
  private final StateMachineVisitor visitor_;
  private CommAddress queryAddress_;

  private static Logger logger_ = Logger.getLogger(GetNebuloObjectModule.class);

  public GetNebuloObjectModule(NebuloAddress nebuloKey) {
    address_ = nebuloKey;
    visitor_ = new StateMachineVisitor();
  }

  /*
   * Constructor that runs newly created module.
   */
  public GetNebuloObjectModule(NebuloAddress nebuloKey, BlockingQueue<Message> dispatcherQueue) {
    address_ = nebuloKey;
    visitor_ = new StateMachineVisitor();
    runThroughDispatcher(dispatcherQueue);
  }

  /*
   * Constructor that runs newly created module in the ADDRESS_GIVEN mode.
   */
  public GetNebuloObjectModule(NebuloAddress nebuloKey, CommAddress replicaAddress,
      BlockingQueue<Message> dispatcherQueue) {
    address_ = nebuloKey;
    queryAddress_ = replicaAddress;
    visitor_ = new StateMachineVisitor();
    runThroughDispatcher(dispatcherQueue);
  }

  /**
   * States of the state machine.
   */
  private enum STATE { INIT, DHT_QUERY, REPLICA_FETCH, FILE_RECEIVED, ADDRESS_GIVEN };

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
        networkQueue_.add(new GetDHTMessage(jobId_,
            new KeyDHT(address_.getAppKey().getKey())));
      } else if (state_ == STATE.ADDRESS_GIVEN) {
        state_ = STATE.REPLICA_FETCH;
        queryReplica(queryAddress_);
      } else {
        logger_.warn("JobInitMessage received in state " + state_.name());
      }
      return null;
    }

    @Override
    public Void visit(ValueDHTMessage message) {
      if (state_ == STATE.DHT_QUERY) {

        // State 2 - Receive reply from DHT and iterate over logical path segments asking
        // for consecutive parts.
        state_ = STATE.REPLICA_FETCH;
        // TODO(bolek): How to avoid casting here? Make ValueDHTMessage generic?
        Metadata metadata = (Metadata) message.getValue().getValue();
        ContractList contractList = metadata.getContractList();
        ReplicationGroup group = contractList.getGroup(address_.getObjectId());
        if (group == null) {
          endWithError(new NebuloException("No peers replicating this object."));
        }
        // TODO(bolek): Ask other replicas if first query is unsuccessful.
        queryReplica(group.getReplicator(0));
      } else {
        logger_.warn("ValueDHTMessage received in state " + state_.name());
      }
      return null;
    }

    public void queryReplica(CommAddress replicaAddress) {
      // Source address will be added by Network module.
      networkQueue_.add(new GetObjectMessage(CryptoUtils.getRandomId().toString(), null, replicaAddress, address_.getObjectId(), jobId_));
    }

    @Override
    public Void visit(SendObjectMessage message) {
      if (state_ == STATE.REPLICA_FETCH) {
        NebuloObject nebuloObject;
        try {
          nebuloObject = (NebuloObject) CryptoUtils.decryptObject(message.encryptedEntity_);
          nebuloObject.setSender(message.getSourceAddress());
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

    @Override
    public Void visit(ReplicatorErrorMessage message) {
      if (state_ == STATE.REPLICA_FETCH) {
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
