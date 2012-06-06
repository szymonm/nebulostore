package org.nebulostore.api;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.appcore.EncryptedObject;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.replicator.messages.SendObjectMessage;
import org.nebulostore.utils.Pair;

/**
 *
 * @author szymonmatejczyk
 *
 */
public class GetEncryptedObjectModule extends GetModule<Pair<EncryptedObject, Set<String>>> {
  private static Logger logger_ = Logger.getLogger(GetModule.class);

  private final GetEncryptedObjectVisitor visitor_ = new GetEncryptedObjectVisitor();

  public GetEncryptedObjectModule(NebuloAddress nebuloKey) {
    super(nebuloKey);
  }

  /**
   * Constructor that runs this module through dispatcher.
   */
  public GetEncryptedObjectModule(NebuloAddress nebuloKey,
      BlockingQueue<Message> dispatcherQueue) {
    super(nebuloKey);
    runThroughDispatcher(dispatcherQueue);
  }


  public GetEncryptedObjectModule(NebuloAddress nebuloKey,
      CommAddress replicaAddress, BlockingQueue<Message> dispatcherQueue) {
    super(nebuloKey, replicaAddress);
    runThroughDispatcher(dispatcherQueue);
  }

  /**
   * Visitor.
   */
  private class GetEncryptedObjectVisitor extends GetModuleVisitor {
    @Override
    public Void visit(SendObjectMessage message) {
      if (state_ == STATE.REPLICA_FETCH) {
        // State 3 - Finally got the file, return it;
        state_ = STATE.FILE_RECEIVED;
        endWithSuccess(new Pair<EncryptedObject, Set<String>>(message.getEncryptedEntity(),
            message.getVersions()));
      } else {
        logger_.warn("SendObjectMessage received in state " + state_);
      }
      return null;
    }

  }


  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }
}
