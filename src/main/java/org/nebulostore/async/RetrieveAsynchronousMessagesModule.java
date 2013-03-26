package org.nebulostore.async;

import com.google.inject.Inject;

import org.apache.log4j.Logger;
import org.nebulostore.addressing.NebuloAddress;
import org.nebulostore.appcore.InstanceMetadata;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.async.messages.AsynchronousMessage;
import org.nebulostore.async.messages.AsynchronousMessagesMessage;
import org.nebulostore.async.messages.DeleteNebuloObjectMessage;
import org.nebulostore.async.messages.UpdateNebuloObjectMessage;
import org.nebulostore.broker.BrokerContext;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.replicator.DeleteObjectException;
import org.nebulostore.replicator.Replicator;


/**
 * Class used to handle GetAsynchronousMessagesInMessage.
 * Retrieves asynchronous messages from synchro peers by creating GetAsynchronousMessagesModule for
 * each synchro-peer.
 * @author szymonmatejczyk
 */
public class RetrieveAsynchronousMessagesModule extends JobModule {
  private static Logger logger_ = Logger.getLogger(RetrieveAsynchronousMessagesModule.class);

  public static final Long INTERVAL = 2000L;

  private CommAddress myAddress_;

  @Inject
  private void setPeerAddress(CommAddress address) {
    myAddress_ = address;
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }
  private final RAMVisitor visitor_ = new RAMVisitor();

  /**
   * Visitor.
   * @author szymonmatejczyk
   */
  private class RAMVisitor extends MessageVisitor<Void> {
    BrokerContext context_ = BrokerContext.getInstance();

    /** Start of download of AM. Requests for Metadata containing inboxHolders. */
    @Override
    public Void visit(JobInitMessage message) {
      logger_.debug("Started asynchronous-messages retrieval.");
      jobId_ = message.getId();
      GetDHTMessage m = new GetDHTMessage(jobId_, myAddress_.toKeyDHT());
      networkQueue_.add(m);
      return null;
    }

    @Override
    public Void visit(ErrorDHTMessage message) {
      error(jobId_, new NebuloException("Unable to get synchro peers from DHT."));
      return null;
    }

    @Override
    public Void visit(ValueDHTMessage message) {
      if (message.getKey().equals(myAddress_.toKeyDHT())) {
        if (message.getValue().getValue() instanceof InstanceMetadata) {
          InstanceMetadata metadata = (InstanceMetadata) message.getValue().getValue();
          context_.myInboxHolders_ = metadata.getInboxHolders();
          logger_.debug("Retrieving AM from " + context_.myInboxHolders_.size() + " peers.");
          //TODO(szm): timeouts
          for (CommAddress inboxHolder : context_.myInboxHolders_) {
            GetAsynchronousMessagesModule messagesModule =
                new GetAsynchronousMessagesModule(networkQueue_, inQueue_, context_, inboxHolder);
            JobInitMessage initializingMessage = new JobInitMessage(messagesModule);
            context_.waitingForMessages_.add(initializingMessage.getId());
          }
          if (context_.myInboxHolders_.size() == 0) {
            endJobModule();
          }
        }
      }
      return null;
    }

    @Override
    public Void visit(AsynchronousMessagesMessage message) {
      if (!context_.waitingForMessages_.remove(message.getId())) {
        logger_.warn("Received not expected message.");
      }

      if (message.getMessages() == null) {
        logger_.debug("Empty AMM received.");
      } else {
        for (AsynchronousMessage m : message.getMessages()) {
          // TODO(szm): Prevent message duplicates
          if (m instanceof UpdateNebuloObjectMessage) {
            // TODO(szm): update file
            logger_.debug("Received update file asynchronous message " +
                ((UpdateNebuloObjectMessage) m).getMessageId());
          } else if (m instanceof DeleteNebuloObjectMessage) {
            NebuloAddress address = ((DeleteNebuloObjectMessage) m).getObjectId();
            logger_.debug("Received delete asynchronous message " + address);
            try {
              Replicator.deleteObject(address.getObjectId());
            } catch (DeleteObjectException e) {
              logger_.error("Unable to delete object after receiving asynchronous message.");
            }
          } else {
            error(message.getId(), new NebuloException("Unknown AsynchronousMessage type."));
          }
        }
      }
      if (context_.waitingForMessages_.isEmpty()) {
        endJobModule();
      }
      return null;
    }


    private void error(String jobId, NebuloException error) {
      logger_.warn("Unable to retrive asynchronous messages: " + error.getMessage());
      endJobModule();
    }
  }

}
