package org.nebulostore.async;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.InstanceID;
import org.nebulostore.appcore.InstanceMetadata;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.async.messages.AsynchronousMessage;
import org.nebulostore.async.messages.AsynchronousMessagesMessage;
import org.nebulostore.async.messages.BrokerErrorMessage;
import org.nebulostore.async.messages.UpdateFileMessage;
import org.nebulostore.broker.BrokerContext;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;

/**
 * Class used to handle GetAsynchronousMessagesInMessage.
 * Retrieves asynchronous messages from synchro peers by creating GetAsynchronousMessagesModule for
 * each synchro-peer.
 * @author szymonmatejczyk
 */
public class RetrieveAsynchronousMessagesModule extends JobModule {
  private static Logger logger_ = Logger.getLogger(RetrieveAsynchronousMessagesModule.class);

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }
  private RAMVisitor visitor_ = new RAMVisitor();

  /**
   * Visitor.
   * @author szymonmatejczyk
   */
  private class RAMVisitor extends MessageVisitor<Void> {
    BrokerContext context_ = BrokerContext.getInstance();

    /** Start of download of AM. Requests for Metadata containing inboxHolders. */
    public Void visit(JobInitMessage message) {
      GetDHTMessage m = new GetDHTMessage(jobId_,
          KeyDHT.fromSerializableObject(context_.instanceID_));
      networkQueue_.add(m);
      return null;
    }

    public Void visit(ErrorDHTMessage message) {
      error(message.getId(), new NebuloException("Unable to get synchro peers from DHT."));
      return null;
    }

    public Void visit(ValueDHTMessage message) {
      if (message.getKey().equals(KeyDHT.fromSerializableObject(context_.instanceID_))) {
        if (message.getValue().getValue() instanceof InstanceMetadata) {
          InstanceMetadata metadata = (InstanceMetadata) message.getValue().getValue();
          context_.myInboxHolders_ = metadata.getInboxHolders();
          //TODO(szm): timeouts
          for (InstanceID inboxHolder : context_.myInboxHolders_) {
            GetAsynchronousMessagesModule messagesModule =
                new GetAsynchronousMessagesModule(outQueue_, networkQueue_, inQueue_, context_,
                    inboxHolder);
            context_.waitingForMessages_.add(messagesModule.getJobId());
          }
        }
      }
      return null;
    }

    public Void visit(AsynchronousMessagesMessage message) {
      if (!context_.waitingForMessages_.remove(message.getId())) {
        logger_.warn("Received not expected message.");
      }

      if (message.getMessages() == null) {
        logger_.debug("Empty AMM received.");
      } else {
        for (AsynchronousMessage m : message.getMessages()) {
          // TODO(szm): Prevent message duplicates
          if (m instanceof UpdateFileMessage) {
            // TODO(szm): update file
            logger_.debug("Received update file asynchronous message " +
                ((UpdateFileMessage) m).getMessageId());
          } else {
            error(message.getId(), new NebuloException(
                "Unknown AsynchronousMessage type."));
          }
        }
      }
      if (context_.waitingForMessages_.isEmpty())
        endJobModule();

      return null;
    }


    private void error(String jobId, NebuloException error) {
      outQueue_.add(new BrokerErrorMessage(jobId, error));
    }
  }

}
