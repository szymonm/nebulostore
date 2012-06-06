package org.nebulostore.api;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.NebuloObject;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.crypto.CryptoException;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.replicator.messages.SendObjectMessage;

/**
 * @author bolek
 * Job module that fetches an existing object from NebuloStore.
 */
public class GetNebuloObjectModule extends GetModule<NebuloObject> {

  private final StateMachineVisitor visitor_;

  private static Logger logger_ = Logger.getLogger(GetNebuloObjectModule.class);

  public GetNebuloObjectModule(NebuloAddress nebuloKey) {
    super(nebuloKey);
    visitor_ = new StateMachineVisitor();
  }

  /*
   * Constructor that runs newly created module.
   */
  public GetNebuloObjectModule(NebuloAddress nebuloKey, BlockingQueue<Message> dispatcherQueue) {
    super(nebuloKey);
    visitor_ = new StateMachineVisitor();
    runThroughDispatcher(dispatcherQueue);
  }

  /*
   * Constructor that runs newly created module in the ADDRESS_GIVEN mode.
   */
  public GetNebuloObjectModule(NebuloAddress nebuloKey, CommAddress replicaAddress,
      BlockingQueue<Message> dispatcherQueue) {
    super(nebuloKey, replicaAddress);
    visitor_ = new StateMachineVisitor();
    runThroughDispatcher(dispatcherQueue);
  }

  /**
   * Visitor.
   */
  private class StateMachineVisitor extends GetModuleVisitor {

    @Override
    public Void visit(SendObjectMessage message) {
      if (state_ == STATE.REPLICA_FETCH) {
        NebuloObject nebuloObject;
        try {
          nebuloObject = (NebuloObject) CryptoUtils.decryptObject(message.getEncryptedEntity());
          nebuloObject.setSender(message.getSourceAddress());
          nebuloObject.setVersions(message.getVersions());
          nebuloObject.setLastCommittedVersion(CryptoUtils.sha(message.getEncryptedEntity()));
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
