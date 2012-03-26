package org.nebulostore.appcore;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.exceptions.UnsupportedMessageException;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.CommSendDataMessage;
import org.nebulostore.communication.messages.DiscoveryMessage;
import org.nebulostore.communication.messages.broker.AsynchronousMessagesMessage;
import org.nebulostore.communication.messages.broker.BrokerErrorMessage;
import org.nebulostore.communication.messages.broker.GetAsynchronousMessagesMessage;
import org.nebulostore.communication.messages.broker.GotAsynchronousMessagesMessage;
import org.nebulostore.communication.messages.broker.StoreAsynchronousMessage;
import org.nebulostore.communication.messages.dht.DHTMessage;
import org.nebulostore.communication.messages.dht.DelDHTMessage;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.InDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.OutDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.communication.messages.pingpong.PingMessage;
import org.nebulostore.communication.messages.pingpong.PongMessage;
import org.nebulostore.communication.messages.testing.FinishTestMessage;
import org.nebulostore.communication.messages.testing.NewPhaseMessage;
import org.nebulostore.communication.messages.testing.TicMessage;
import org.nebulostore.communication.messages.testing.TocMessage;
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;
import org.nebulostore.replicator.messages.ConfirmationMessage;
import org.nebulostore.replicator.messages.DeleteObjectMessage;
import org.nebulostore.replicator.messages.GetObjectMessage;
import org.nebulostore.replicator.messages.ReplicatorErrorMessage;
import org.nebulostore.replicator.messages.SendObjectMessage;
import org.nebulostore.replicator.messages.StoreObjectMessage;

/**
 * Generic Message visitor class.
 * @param <R>
 *    return type.
 */
public abstract class MessageVisitor<R> {
  /* Common action for all messages that are not handled. */
  protected R visitDefault(Message message) throws NebuloException {
    throw new UnsupportedMessageException(message.getClass().getName());
  }

  /* Base class. */
  public R visit(Message message) throws NebuloException {
    return visitDefault(message);
  }

  /* Dispatcher messages. */
  public R visit(JobEndedMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(JobInitMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(KillDispatcherMessage message) throws NebuloException {
    return visitDefault(message);
  }

  /* Replicator messages. */
  public R visit(GetObjectMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(DeleteObjectMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(SendObjectMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(StoreObjectMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(ConfirmationMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(ReplicatorErrorMessage message) throws NebuloException {
    return visitDefault(message);
  }

  /* Network messages. */
  public R visit(CommMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(CommPeerFoundMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(CommSendDataMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(DiscoveryMessage message) throws NebuloException {
    return visitDefault(message);
  }

  /* DHT messages. */
  public R visit(DHTMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(DelDHTMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(ErrorDHTMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(GetDHTMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(InDHTMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(OkDHTMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(OutDHTMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(PutDHTMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(ValueDHTMessage message) throws NebuloException {
    return visitDefault(message);
  }

  /* Broker Asynchronous Messaging messages. */
  public R visit(AsynchronousMessagesMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(BrokerErrorMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(GetAsynchronousMessagesMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(GotAsynchronousMessagesMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(StoreAsynchronousMessage message) throws NebuloException {
    return visitDefault(message);
  }

  /* TestingModule. */
  public R visit(FinishTestMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(NewPhaseMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(TicMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(TocMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(PongMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(PingMessage message) throws NebuloException {
    return visitDefault(message);
  }
}
