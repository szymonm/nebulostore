package org.nebulostore.appcore;

import org.nebulostore.api.DeleteNebuloObjectModule.DeleteTimeoutMessage;
import org.nebulostore.api.WriteNebuloObjectModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.exceptions.UnsupportedMessageException;
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
import org.nebulostore.communication.dht.ReconfigureDHTTestMessage;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.CommSendDataMessage;
import org.nebulostore.communication.messages.DataExchangeMessage;
import org.nebulostore.communication.messages.DiscoveryMessage;
import org.nebulostore.communication.messages.ErrorCommMessage;
import org.nebulostore.communication.messages.ReconfigureDHTAckMessage;
import org.nebulostore.communication.messages.ReconfigureDHTMessage;
import org.nebulostore.communication.messages.ReconfigureMessagesTestMessage;
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
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.dispatcher.messages.KillDispatcherMessage;
import org.nebulostore.networkmonitor.messages.ConnectionTestMessage;
import org.nebulostore.networkmonitor.messages.ConnectionTestResponseMessage;
import org.nebulostore.networkmonitor.messages.RandomPeersSampleMessage;
import org.nebulostore.query.messages.GossipExecutorsMessage;
import org.nebulostore.query.messages.QueryAcceptedMessage;
import org.nebulostore.query.messages.QueryErrorMessage;
import org.nebulostore.query.messages.QueryMessage;
import org.nebulostore.query.messages.QueryResultsMessage;
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
import org.nebulostore.testing.messages.ErrorTestMessage;
import org.nebulostore.testing.messages.FinishTestMessage;
import org.nebulostore.testing.messages.GatherStatsMessage;
import org.nebulostore.testing.messages.NewPhaseMessage;
import org.nebulostore.testing.messages.ReconfigureTestMessage;
import org.nebulostore.testing.messages.TestInitMessage;
import org.nebulostore.testing.messages.TestStatsMessage;
import org.nebulostore.testing.messages.TicAckMessage;
import org.nebulostore.testing.messages.TicMessage;
import org.nebulostore.testing.messages.TocAckMessage;
import org.nebulostore.testing.messages.TocMessage;
import org.nebulostore.timer.InitSimpleTimerTestMessage;
import org.nebulostore.timer.TimerTestMessage;

/**
 * Generic Message visitor class.
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

  public R visit(QueryToStoreObjectMessage message) throws NebuloException {
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

  /* API messages. */
  public R visit(DeleteTimeoutMessage message) throws NebuloException {
    return visitDefault(message);
  }

  /* Broker messages. */
  public R visit(ContractOfferMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(OfferReplyMessage message) throws NebuloException {
    return visitDefault(message);
  }

  /* Broker Asynchronous Messaging messages. */
  public R visit(AsynchronousMessagesMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(BrokerErrorMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(DeleteNebuloObjectMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(GetAsynchronousMessagesIn message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(GetAsynchronousMessagesMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(GotAsynchronousMessagesMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(NetworkContextChangedMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(StoreAsynchronousMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(UpdateFileMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(UpdateNebuloObjectMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(UpdateSmallNebuloObjectMessage message) throws NebuloException {
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

  public R visit(TestInitMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(ReconfigureTestMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(ErrorTestMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(TestStatsMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(GatherStatsMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(ReconfigureDHTAckMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(ReconfigureDHTMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(DataExchangeMessage message) throws NebuloException {
    return visitDefault(message);
  }

  // Timer module tests.
  public R visit(TimerTestMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(InitSimpleTimerTestMessage message) throws NebuloException {
    return visitDefault(message);
  }
  public R visit(ErrorCommMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(UpdateRejectMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(UpdateWithholdMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(QueryMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(QueryAcceptedMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(QueryErrorMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(QueryResultsMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(GossipExecutorsMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(WriteNebuloObjectModule.TransactionAnswerInMessage message)
    throws NebuloException {
    return visitDefault(message);
  }

  public R visit(TransactionResultMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(ObjectOutdatedMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(TocAckMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(TicAckMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(ReconfigureMessagesTestMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(ReconfigureDHTTestMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(RandomPeersSampleMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(ConnectionTestMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(ConnectionTestResponseMessage message) throws NebuloException {
    return visitDefault(message);
  }

  public R visit(TimeoutMessage message) throws NebuloException {
    return visitDefault(message);
  }
}
