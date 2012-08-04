package org.nebulostore.api;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.AppKey;
import org.nebulostore.addressing.ContractList;
import org.nebulostore.addressing.IntervalCollisionException;
import org.nebulostore.addressing.ReplicationGroup;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.Metadata;
import org.nebulostore.appcore.ReturningJobModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.dht.ValueDHT;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;

/**
 * @author bolek
 * Job module that realizes putKey() API function.
 */
public class PutKeyModule extends ReturningJobModule<Void> {

  private final AppKey appKey_;
  private final StateMachineVisitor visitor_;
  private ReplicationGroup replicationGroup_;

  private static Logger logger_ = Logger.getLogger(PutKeyModule.class);

  public PutKeyModule(AppKey appKey) {
    appKey_ = appKey;
    visitor_ = new StateMachineVisitor();
  }

  /*
   * Constructor that runs newly created module.
   */
  public PutKeyModule(AppKey appKey, ReplicationGroup replicationGroup,
      BlockingQueue<Message> dispatcherQueue) {
    appKey_ = appKey;
    visitor_ = new StateMachineVisitor();
    replicationGroup_ = replicationGroup;
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

    public StateMachineVisitor() {
      state_ = STATE.INIT;
    }

    @Override
    public Void visit(JobInitMessage message) {
      if (state_ == STATE.INIT) {
        // State 1 - Send appKey to DHT and wait for reply.
        state_ = STATE.DHT_INSERT;
        jobId_ = message.getId();

        // List of top-dir replicators stored in DHT.
        ContractList contractList = new ContractList();
        try {
          // TODO(bolek, marcin): This will be replaced with a DHT update function.
          contractList.addGroup(replicationGroup_);
        } catch (IntervalCollisionException exception) {
          endWithError(new NebuloException("Error while creating replication group", exception));
        }
        logger_.debug("Updating top dir in DHT, contracts list: " + contractList.toString());
        networkQueue_.add(new PutDHTMessage(jobId_, new KeyDHT(appKey_.getKey()),
            new ValueDHT(new Metadata(appKey_, contractList))));
      } else {
        logger_.warn("JobInitMessage received in state " + state_);
      }
      return null;
    }

    @Override
    public Void visit(OkDHTMessage message) {
      if (state_ == STATE.DHT_INSERT) {
        logger_.debug("Successfully updated top dir in DHT");
        endWithSuccess(null);
      } else {
        logger_.warn("OkDHTMessage received in state " + state_);
      }
      return null;
    }

    @Override
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
