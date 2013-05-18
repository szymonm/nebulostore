package org.nebulostore.async;

import java.util.List;

import com.google.inject.Inject;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.InstanceMetadata;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.appcore.modules.JobModule;
import org.nebulostore.broker.BrokerContext;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.core.ValueDHT;
import org.nebulostore.communication.dht.messages.ErrorDHTMessage;
import org.nebulostore.communication.dht.messages.GetDHTMessage;
import org.nebulostore.communication.dht.messages.OkDHTMessage;
import org.nebulostore.communication.dht.messages.PutDHTMessage;
import org.nebulostore.communication.dht.messages.ValueDHTMessage;
import org.nebulostore.dispatcher.JobInitMessage;
import org.nebulostore.networkmonitor.NetworkContext;


/**
 * Module that adds to DHT new SynchroPeer - synchroPeer_.
 *
 * Can be run also without synchroPeer_. In this case, gets last found peer from NetworkContext and
 * uses it as synchro-peer.
 *
 * @author sm262956
 *
 */
public class AddSynchroPeerModule extends JobModule {
  private static Logger logger_ = Logger.getLogger(AddSynchroPeerModule.class);

  private final MessageVisitor<Void> visitor_ = new Visitor();

  private CommAddress synchroPeer_;
  private CommAddress myAddress_;

  @Inject
  private void setPeerAddress(CommAddress address) {
    myAddress_ = address;
  }

  public AddSynchroPeerModule() {
  }

  public AddSynchroPeerModule(CommAddress synchroPeer) {
    synchroPeer_ = synchroPeer;
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  /**
   * Visitor.
   */
  protected class Visitor extends MessageVisitor<Void> {
    BrokerContext context_ = BrokerContext.getInstance();

    public Void visit(JobInitMessage message) {
      jobId_ = message.getId();
      // If synchroPeer_ is not set we use the last one found by the NetworkContext.
      if (synchroPeer_ == null) {
        List<CommAddress> synchroPeers = NetworkContext.getInstance().getKnownPeers();
        synchroPeer_ = synchroPeers.get(synchroPeers.size() - 1);
      }
      if (synchroPeer_ == null) {
        logger_.warn("Empy synchro peer got as the last from NetworkContext.");
        endJobModule();
        return null;
      }
      GetDHTMessage m = new GetDHTMessage(jobId_, myAddress_.toKeyDHT());
      networkQueue_.add(m);
      return null;
    }

    public Void visit(ErrorDHTMessage message) {
      error("Unable to get synchro peers from DHT.");
      endJobModule();
      return null;
    }

    public Void visit(ValueDHTMessage message) {
      if (message.getKey().equals(myAddress_.toKeyDHT())) {
        if (message.getValue().getValue() instanceof InstanceMetadata) {
          InstanceMetadata metadata = (InstanceMetadata) message.getValue().getValue();
          // list merging -> on dht level
          metadata.getInboxHolders().add(synchroPeer_);
          context_.setMyInboxHolders(metadata.getInboxHolders());

          PutDHTMessage m = new PutDHTMessage(jobId_, myAddress_.toKeyDHT(),
              new ValueDHT(metadata));
          networkQueue_.add(m);
        } else {
          error("unexpected value type");
          endJobModule();
        }
      } else {
        error("unexpected message of type ValueDHTMessage");
      }
      return null;
    }

    public Void visit(OkDHTMessage message) {
      logger_.debug("Successfully added synchro peer: " + synchroPeer_.toString() + " to DHT.");
      endJobModule();
      return null;
    }

    private void error(String message) {
      logger_.warn(message);
    }
  }
}
