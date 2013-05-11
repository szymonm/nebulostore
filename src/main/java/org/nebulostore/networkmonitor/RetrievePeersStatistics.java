package org.nebulostore.networkmonitor;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.nebulostore.appcore.InstanceMetadata;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.ReturningJobModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.messages.ErrorDHTMessage;
import org.nebulostore.communication.dht.messages.GetDHTMessage;
import org.nebulostore.communication.dht.messages.ValueDHTMessage;
import org.nebulostore.dispatcher.JobInitMessage;

/**
 * Module that queries DHT for peer's statistics.
 * @author szymonmatejczyk
 */
public class RetrievePeersStatistics extends ReturningJobModule<ConcurrentLinkedQueue
    <PeerConnectionSurvey>> {
  private final CommAddress peer_;

  public RetrievePeersStatistics(CommAddress peer) {
    peer_ = peer;
  }

  private final RPSVisitor visitor_ = new RPSVisitor();

  /**
   * Visitor.
   */
  protected class RPSVisitor extends MessageVisitor<Void> {
    public Void visit(JobInitMessage message) {
      jobId_ = message.getId();
      networkQueue_.add(new GetDHTMessage(message.getId(), peer_.toKeyDHT()));
      return null;
    }

    public Void visit(ValueDHTMessage message) {
      InstanceMetadata metadata = (InstanceMetadata) message.getValue().getValue();
      endWithSuccess(metadata.getStatistics());
      return null;
    }

    public Void visit(ErrorDHTMessage message) {
      endWithError(new NebuloException("DHT Error"));
      return null;
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }
}
