package org.nebulostore.async;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.GlobalContext;
import org.nebulostore.appcore.InstanceID;
import org.nebulostore.appcore.InstanceMetadata;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.broker.BrokerContext;
import org.nebulostore.communication.dht.ValueDHT;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;
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

  private InstanceID synchroPeer_;

  public AddSynchroPeerModule() {
  }

  public AddSynchroPeerModule(InstanceID synchroPeer) {
    synchroPeer_ = synchroPeer;
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  /**
   * Visitor.
   */
  private class Visitor extends MessageVisitor<Void> {
    InstanceID myInstanceId_ = GlobalContext.getInstance().getInstanceID();
    BrokerContext context_ = BrokerContext.getInstance();

    @Override
    public Void visit(JobInitMessage message) {
      jobId_ = message.getId();
      // If synchroPeer_ is not set we use the last one found by the NetworkContext.
      if (synchroPeer_ == null) {
        synchroPeer_ = new InstanceID(NetworkContext.getInstance().getKnownPeers().lastElement());
      }
      GetDHTMessage m = new GetDHTMessage(jobId_, myInstanceId_.toKeyDHT());
      networkQueue_.add(m);
      return null;
    }

    @Override
    public Void visit(ErrorDHTMessage message) {
      error("Unable to get synchro peers from DHT.");
      endJobModule();
      return null;
    }

    @Override
    public Void visit(ValueDHTMessage message) {
      if (message.getKey().equals(myInstanceId_.toKeyDHT())) {
        if (message.getValue().getValue() instanceof InstanceMetadata) {
          InstanceMetadata metadata = (InstanceMetadata) message.getValue().getValue();
          // list merging -> on dht level
          metadata.getInboxHolders().add(synchroPeer_);
          context_.myInboxHolders_ = metadata.getInboxHolders();

          PutDHTMessage m = new PutDHTMessage(jobId_, myInstanceId_.toKeyDHT(),
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

    @Override
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
