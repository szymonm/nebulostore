package org.nebulostore.networkmonitor;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.InstanceMetadata;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.TimeoutMessage;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.dht.ValueDHT;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.networkmonitor.messages.ConnectionTestMessage;
import org.nebulostore.networkmonitor.messages.ConnectionTestResponseMessage;
import org.nebulostore.timer.TimerContext;

/**
 * Tests peers connection data(ex. availability, bandwidth, ...). Appends result to DHT.
 * @author szymonmatejczyk
 */
public class TestPeersConnectionModule extends JobModule {
  private static Logger logger_ = Logger.getLogger(TestPeersConnectionModule.class);

  private final CommAddress peer_;

  private static final long TIMEOUT_MILLIS = 5000L;

  public TestPeersConnectionModule(CommAddress peer) {
    peer_ = peer;
  }

  TPCVisitor visitor_ = new TPCVisitor();

  /**
   * Visitor.
   */
  private class TPCVisitor extends MessageVisitor<Void> {
    long sendTime_;
    ValueDHTMessage valueDHTMessage_;
    List<PeerConnectionSurvey> stats_ = new LinkedList<PeerConnectionSurvey>();

    @Override
    public Void visit(JobInitMessage message) {
      jobId_ = message.getId();
      networkQueue_.add(new GetDHTMessage(message.getId(), peer_.toKeyDHT()));
      sendTime_ = System.currentTimeMillis();
      networkQueue_.add(new ConnectionTestMessage(null,  peer_));
      TimerContext.getInstance().notifyWithTimeoutMessageAfter(getJobId(), TIMEOUT_MILLIS);
      return null;
    }

    @Override
    public Void visit(ConnectionTestResponseMessage message) {
      // TODO(szm): other statistics
      // TODO(szm): bandwidth??
      stats_.add(new PeerConnectionSurvey(CommunicationPeer.getPeerAddress(),
          System.currentTimeMillis(), ConnectionAttribute.AVAILABILITY, 1.0));
      stats_.add(new PeerConnectionSurvey(CommunicationPeer.getPeerAddress(),
          System.currentTimeMillis(), ConnectionAttribute.LATENCY,
          (sendTime_ - System.currentTimeMillis()) / 2));

      if (valueDHTMessage_ != null) {
        appendStatisticsAndFinish(stats_, valueDHTMessage_);
      }
      return null;
    }

    @Override
    public Void visit(ValueDHTMessage message) {
      if (!stats_.isEmpty()) {
        appendStatisticsAndFinish(stats_, message);
      } else {
        valueDHTMessage_ = message;
      }
      return null;
    }


    @Override
    public Void visit(TimeoutMessage message) {
      logger_.warn("Timeout.");
      endJobModule();
      return null;
    }

    private void appendStatisticsAndFinish(List<PeerConnectionSurvey> stats,
        ValueDHTMessage message) {
      InstanceMetadata metadata = (InstanceMetadata) message.getValue().getValue();
      for (PeerConnectionSurvey pcs : stats) {
        metadata.getStatistics().add(pcs);
      }
      //TODO(szm): A co z synchronizacja na poziomie DHT??? -> odpowiedni merge
      networkQueue_.add(new PutDHTMessage(getJobId(), peer_.toKeyDHT(), new ValueDHT(metadata)));
      TimerContext.getInstance().cancelNotifications(getJobId());
      endJobModule();
    }

  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }
}
