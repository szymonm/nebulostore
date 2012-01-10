package org.nebulostore.appcore;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.exceptions.UnsupportedMessageException;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.CommSendDataMessage;
import org.nebulostore.communication.messages.DiscoveryMessage;
import org.nebulostore.communication.messages.dht.DHTMessage;
import org.nebulostore.communication.messages.dht.DelDHTMessage;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.InDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.OutDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;
import org.nebulostore.replicator.messages.ConfirmationMessage;
import org.nebulostore.replicator.messages.DeleteObjectMessage;
import org.nebulostore.replicator.messages.GetObjectMessage;
import org.nebulostore.replicator.messages.ReplicatorErrorMessage;
import org.nebulostore.replicator.messages.SendObjectMessage;
import org.nebulostore.replicator.messages.StoreObjectMessage;
import org.nebulostore.replicator.messages.UpdateObjectMessage;

/**
 * Generic Message visitor class.
 * @param <R>
 *    return type.
 */
public abstract class MessageVisitor<R> {
  /* Common action for all messages that are not handled. */
  private R action(String name) throws NebuloException {
    throw new UnsupportedMessageException(name);
  }

  /* Base class. */
  public R visit(Message message) throws NebuloException {
    return action("Message");
  }

  /* Dispatcher messages. */
  public R visit(JobEndedMessage message) throws NebuloException {
    return action("JobEndedMessage");
  }
  public R visit(JobInitMessage message) throws NebuloException {
    return action("JobInitMessage");
  }
  public R visit(KillDispatcherMessage message) throws NebuloException {
    return action("KillDispatcherMessage");
  }

  /* Replicator messages. */
  public R visit(GetObjectMessage message) throws NebuloException {
    return action("GetObjectMessage");
  }
  public R visit(DeleteObjectMessage message) throws NebuloException {
    return action("DeleteObjectMessage");
  }
  public R visit(SendObjectMessage message) throws NebuloException {
    return action("SendObjectMessage");
  }
  public R visit(StoreObjectMessage message) throws NebuloException {
    return action("StoreObjectMessage");
  }
  public R visit(ConfirmationMessage message) throws NebuloException {
    return action("StoreObjectMessage");
  }
  public R visit(ReplicatorErrorMessage message) throws NebuloException {
    return action("StoreObjectMessage");
  }
  public R visit(UpdateObjectMessage message) throws NebuloException {
    return action("StoreObjectMessage");
  }

  /* Network messages. */
  public R visit(CommMessage message) throws NebuloException {
    return action("CommMessage");
  }
  public R visit(CommPeerFoundMessage message) throws NebuloException {
    return action("CommPeerFoundMessage");
  }
  public R visit(CommSendDataMessage message) throws NebuloException {
    return action("CommSendDataMessage");
  }
  public R visit(DiscoveryMessage message) throws NebuloException {
    return action("DiscoveryMessage");
  }

  /* DHT messages. */
  public R visit(DHTMessage message) throws NebuloException {
    return action("DHTMessage");
  }
  public R visit(DelDHTMessage message) throws NebuloException {
    return action("DelDHTMessage");
  }
  public R visit(ErrorDHTMessage message) throws NebuloException {
    return action("ErrorDHTMessage");
  }
  public R visit(GetDHTMessage message) throws NebuloException {
    return action("GetDHTMessage");
  }
  public R visit(InDHTMessage message) throws NebuloException {
    return action("InDHTMessage");
  }
  public R visit(OkDHTMessage message) throws NebuloException {
    return action("OkDHTMessage");
  }
  public R visit(OutDHTMessage message) throws NebuloException {
    return action("OutDHTMessage");
  }
  public R visit(PutDHTMessage message) throws NebuloException {
    return action("PutDHTMessage");
  }
  public R visit(ValueDHTMessage message) throws NebuloException {
    return action("ValueDHTMessage");
  }
}
