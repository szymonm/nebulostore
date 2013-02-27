package org.nebulostore.api;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.model.NebuloObject;
import org.nebulostore.appcore.model.ObjectGetter;
import org.nebulostore.crypto.CryptoException;
import org.nebulostore.crypto.CryptoUtils;
import org.nebulostore.replicator.messages.SendObjectMessage;

/**
 * Job module that fetches an existing object from NebuloStore.
 * @author Bolek Kulbabinski
 */
public class GetNebuloObjectModule extends GetModule<NebuloObject> implements ObjectGetter {
  private static Logger logger_ = Logger.getLogger(GetNebuloObjectModule.class);
  private final StateMachineVisitor visitor_ = new StateMachineVisitor();

  @Override
  public NebuloObject awaitResult(int timeoutSec) throws NebuloException {
    return getResult(timeoutSec);
  }

  /**
   * Visitor.
   */
  private class StateMachineVisitor extends GetModuleVisitor {

    @Override
    public Void visit(SendObjectMessage message) {
      if (state_ == STATE.REPLICA_FETCH) {
        logger_.debug("Got object - returning");
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
