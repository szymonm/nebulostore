package org.nebulostore.appcore;

import org.nebulostore.api.WriteNebuloObjectModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.exceptions.UnsupportedMessageException;
import org.nebulostore.async.messages.AsynchronousMessage;
import org.nebulostore.async.messages.AsynchronousMessagesMessage;
import org.nebulostore.async.messages.BrokerErrorMessage;
import org.nebulostore.async.messages.DeleteNebuloObjectMessage;
import org.nebulostore.async.messages.GetAsynchronousMessagesIn;
import org.nebulostore.async.messages.GetAsynchronousMessagesMessage;
import org.nebulostore.async.messages.GotAsynchronousMessagesMessage;
import org.nebulostore.async.messages.NetworkContextChangedMessage;
import org.nebulostore.async.messages.StoreAsynchronousMessage;
import org.nebulostore.async.messages.UpdateFileMessage;
import org.nebulostore.async.messages.UpdateNebuloObjectMessage;
import org.nebulostore.async.messages.UpdateSmallNebuloObjectMessage;
import org.nebulostore.broker.messages.ContractOfferMessage;
import org.nebulostore.broker.messages.OfferReplyMessage;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.CommSendDataMessage;
import org.nebulostore.communication.messages.DiscoveryMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;
import org.nebulostore.communication.messages.ReconfigureDHTAckMessage;
import org.nebulostore.communication.messages.ReconfigureDHTMessage;
import org.nebulostore.communication.messages.bdbdht.BdbMessageWrapper;
import org.nebulostore.communication.messages.bdbdht.HolderAdvertisementMessage;
import org.nebulostore.communication.messages.dht.DHTMessage;
import org.nebulostore.communication.messages.dht.DelDHTMessage;
import org.nebulostore.communication.messages.dht.ErrorDHTMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.InDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.OutDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.communication.messages.gossip.PeerGossipMessage;
import org.nebulostore.conductor.messages.ErrorMessage;
import org.nebulostore.conductor.messages.FinishMessage;
import org.nebulostore.conductor.messages.GatherStatsMessage;
import org.nebulostore.conductor.messages.InitMessage;
import org.nebulostore.conductor.messages.NewPhaseMessage;
import org.nebulostore.conductor.messages.ReconfigurationMessage;
import org.nebulostore.conductor.messages.StatsMessage;
import org.nebulostore.conductor.messages.TicMessage;
import org.nebulostore.conductor.messages.TocMessage;
import org.nebulostore.conductor.messages.UserCommMessage;
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;
import org.nebulostore.networkmonitor.messages.ConnectionTestMessage;
import org.nebulostore.networkmonitor.messages.ConnectionTestResponseMessage;
import org.nebulostore.networkmonitor.messages.RandomPeersSampleMessage;
import org.nebulostore.replicator.messages.ConfirmationMessage;
import org.nebulostore.replicator.messages.DeleteObjectMessage;
import org.nebulostore.replicator.messages.GetObjectMessage;
import org.nebulostore.replicator.messages.ObjectOutdatedMessage;
import org.nebulostore.replicator.messages.QueryToStoreObjectMessage;
import org.nebulostore.replicator.messages.ReplicatorErrorMessage;
import org.nebulostore.replicator.messages.SendObjectMessage;
import org.nebulostore.replicator.messages.TransactionResultMessage;
import org.nebulostore.replicator.messages.UpdateRejectMessage;
import org.nebulostore.replicator.messages.UpdateWithholdMessage;
import org.nebulostore.subscription.messages.NotifySubscriberMessage;

/**
 * Generic Message visitor class. All 'visit' methods should call handlers for base classes.
 * @param <R>
 *          return type.
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

  public R visit(EndModuleMessage message) throws NebuloException {
    return visitDefault((Message) message);
  }

  /* Dispatcher messages. */
  public R visit(JobEndedMessage message) throws NebuloException {
    return visit((Message) message);
  }

  public R visit(JobInitMessage message) throws NebuloException {
    return visit((Message) message);
  }

  public R visit(KillDispatcherMessage message) throws NebuloException {
    return visit((Message) message);
  }

  /* Replicator messages. */
  public R visit(GetObjectMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(DeleteObjectMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(SendObjectMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(QueryToStoreObjectMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(ConfirmationMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(ReplicatorErrorMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  /* Network messages. */
  public R visit(CommMessage message) throws NebuloException {
    return visit((Message) message);
  }

  public R visit(CommPeerFoundMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(CommSendDataMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(DiscoveryMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(PeerGossipMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  /* DHT messages. */
  public R visit(BdbMessageWrapper message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(DHTMessage message) throws NebuloException {
    return visit((Message) message);
  }

  public R visit(DelDHTMessage message) throws NebuloException {
    return visit((InDHTMessage) message);
  }

  public R visit(ErrorDHTMessage message) throws NebuloException {
    return visit((OutDHTMessage) message);
  }

  public R visit(GetDHTMessage message) throws NebuloException {
    return visit((InDHTMessage) message);
  }

  public R visit(HolderAdvertisementMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(InDHTMessage message) throws NebuloException {
    return visit((DHTMessage) message);
  }

  public R visit(OkDHTMessage message) throws NebuloException {
    return visit((OutDHTMessage) message);
  }

  public R visit(OutDHTMessage message) throws NebuloException {
    return visit((DHTMessage) message);
  }

  public R visit(PutDHTMessage message) throws NebuloException {
    return visit((InDHTMessage) message);
  }

  public R visit(ValueDHTMessage message) throws NebuloException {
    return visit((OutDHTMessage) message);
  }

  /* Broker messages. */
  public R visit(ContractOfferMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(OfferReplyMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  /* Broker Asynchronous Messaging messages. */
  public R visit(AsynchronousMessagesMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(AsynchronousMessage message) throws NebuloException {
    return visit((Message) message);
  }

  public R visit(BrokerErrorMessage message) throws NebuloException {
    return visit((Message) message);
  }

  public R visit(DeleteNebuloObjectMessage message) throws NebuloException {
    return visit((AsynchronousMessage) message);
  }

  public R visit(GetAsynchronousMessagesIn message) throws NebuloException {
    return visit((Message) message);
  }

  public R visit(GetAsynchronousMessagesMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(GotAsynchronousMessagesMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(NetworkContextChangedMessage message) throws NebuloException {
    return visit((Message) message);
  }

  public R visit(StoreAsynchronousMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(UpdateFileMessage message) throws NebuloException {
    return visit((AsynchronousMessage) message);
  }

  public R visit(UpdateNebuloObjectMessage message) throws NebuloException {
    return visit((AsynchronousMessage) message);
  }

  public R visit(UpdateSmallNebuloObjectMessage message) throws NebuloException {
    return visit((AsynchronousMessage) message);
  }

  /* TestingModule. */
  public R visit(FinishMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(NewPhaseMessage message) throws NebuloException {
    return visit((Message) message);
  }

  public R visit(TicMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(TocMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(InitMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(ReconfigurationMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(ErrorMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(StatsMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(GatherStatsMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(UserCommMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(ReconfigureDHTAckMessage message) throws NebuloException {
    return visit((Message) message);
  }

  public R visit(ReconfigureDHTMessage message) throws NebuloException {
    return visit((Message) message);
  }

  // Timer module tests.
  public R visit(ErrorCommMessage message) throws NebuloException {
    return visit((Message) message);
  }

  public R visit(UpdateRejectMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(UpdateWithholdMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(WriteNebuloObjectModule.TransactionAnswerInMessage message)
    throws NebuloException {
    return visit((Message) message);
  }

  public R visit(TransactionResultMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(ObjectOutdatedMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(RandomPeersSampleMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(ConnectionTestMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(ConnectionTestResponseMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }

  public R visit(TimeoutMessage message) throws NebuloException {
    return visit((Message) message);
  }

  //Subscriptions
  public R visit(NotifySubscriberMessage message) throws NebuloException {
    return visit((CommMessage) message);
  }
}
