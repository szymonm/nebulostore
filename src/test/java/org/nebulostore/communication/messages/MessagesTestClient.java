package org.nebulostore.communication.messages;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.testing.TestStatistics;
import org.nebulostore.testing.TestingModule;
import org.nebulostore.testing.messages.GatherStatsMessage;
import org.nebulostore.testing.messages.NewPhaseMessage;
import org.nebulostore.testing.messages.TestStatsMessage;

/**
 * Testing client module of communication layer.
 *
 * @author Marcin Walas
 */
public class MessagesTestClient extends TestingModule implements Serializable {

  private static Logger logger_ = Logger.getLogger(MessagesTestClient.class);

  private static final long serialVersionUID = 7223339848299726281L;

  private final int testPhases_;
  private final int messagesForPhase_;
  private CommAddress[] allClients_ = new CommAddress[0];
  private final TestStatistics stats_ = new TestStatistics();
  private final String payload_;
  private int expectedInClients_;

  private MessagesVisitor messagesVisitor_;

  public MessagesTestClient(String serverJobId, int testPhases,
      int messagesForPhase, CommAddress[] commAddresses, String payload) {
    super(serverJobId);
    testPhases_ = testPhases;
    messagesForPhase_ = messagesForPhase;
    payload_ = payload;

    stats_.setDouble("all", 0.0);
    stats_.setDouble("lost", 0.0);
  }

  @Override
  protected void initVisitors() {
    visitors_ = new TestingModuleVisitor[testPhases_ + 1];

    messagesVisitor_ = new MessagesVisitor();
    visitors_[0] = new EmptyInitializationVisitor();
    visitors_[1] = new ConfigurationVisitor();
    for (int i = 2; i < testPhases_; i++) {
      visitors_[i] = messagesVisitor_;
    }
    visitors_[testPhases_] = new StatisticsGatherVisitor();
  }

  @Override
  public void endModule() {
    messagesVisitor_.close();
    super.endModule();
  }

  final class ConfigurationVisitor extends EmptyInitializationVisitor {

    @Override
    public Void visit(ReconfigureMessagesTestMessage message) {
      logger_.info("Got reconfiguration message with clients set: " +
          message.getClients());
      allClients_ = message.getClients().toArray(new CommAddress[0]);
      expectedInClients_ = message.getExpectedInClients();
      phaseFinished();
      return null;
    }
  }

  final class StatisticsGatherVisitor extends TestingModuleVisitor {

    @Override
    public Void visit(NewPhaseMessage message) {
      messagesVisitor_.close();
      return null;
    }

    @Override
    public Void visit(GatherStatsMessage message) {
      logger_.debug("Returning stats on request...");
      networkQueue_.add(new TestStatsMessage(serverJobId_, CommunicationPeer
          .getPeerAddress(), server_, stats_));
      return null;
    }

  }

  final class MessagesVisitor extends TestingModuleVisitor {

    private Set<String> receivedMessages_ = new HashSet<String>();
    private boolean phaseFinished_;

    Timer phaseCheckTimer_;
    private final PhaseCheckTask phaseCheckTask_;
    private boolean checkNotInitialized_ = true;

    public MessagesVisitor() {
      phaseCheckTimer_ = new Timer();
      phaseCheckTask_ = new PhaseCheckTask();

    }

    public void close() {
      phaseCheckTimer_.cancel();
    }

    final class PhaseCheckTask extends TimerTask {

      private int lastSeenReceivedSize_ = -1;

      public void refreshCheck() {
        lastSeenReceivedSize_ = -1;
      }

      @Override
      public void run() {
        if (!phaseFinished_) {
          if (lastSeenReceivedSize_ == receivedMessages_.size() &&
              (!phaseFinished_)) {
            int lost = expectedInClients_ * messagesForPhase_ -
                lastSeenReceivedSize_;
            logger_.info("Forcing phase shutdown with lost messages: " + lost);
            stats_.setDouble("lost", stats_.getDouble("lost") + lost);
            phaseFinished_ = true;
            lastSeenReceivedSize_ = -1;
            phaseFinished();
          }
          lastSeenReceivedSize_ = receivedMessages_.size();
        } else {
          lastSeenReceivedSize_ = -1;
        }
      }
    }

    @Override
    public Void visit(NewPhaseMessage message) {
      refreshVisitor();

      logger_.info("Sending messages to remote hosts...");
      for (CommAddress destination : allClients_) {
        for (int i = 0; i < messagesForPhase_; i++) {
          networkQueue_.add(new DataExchangeMessage(jobId_, CommunicationPeer
              .getPeerAddress(), destination, payload_, phase_, i));
        }
      }
      stats_.setDouble("all", stats_.getDouble("all") +
          new Double(allClients_.length * messagesForPhase_));
      logger_.info("Sending messages to remote hosts. DONE.");
      return null;
    }

    private void refreshVisitor() {
      if (checkNotInitialized_) {
        // 1000 ms set
        phaseCheckTimer_.schedule(phaseCheckTask_, 1000, 1000);
        checkNotInitialized_ = false;
      }
      phaseFinished_ = false;
      receivedMessages_ = new HashSet<String>();
    }

    @Override
    public Void visit(ErrorCommMessage message) {
      logger_.debug("Received ErrorCommMessage. Retrying...");
      networkQueue_.add(message.getMessage());
      return null;
    }

    @Override
    public Void visit(DataExchangeMessage message) {

      if (receivedMessages_.contains(message.getSourceAddress().toString() +
          message.getCounterVal()) ||
          message.getPhase() != phase_) {
        logger_.debug("Received stalled or duplicated DataExchangeMessage");
        return null;
      }
      logger_.debug("Received new DataExchangeMessage");

      receivedMessages_.add(message.getSourceAddress().toString() +
          message.getCounterVal());

      if (!phaseFinished_ &&
          receivedMessages_.size() >= expectedInClients_ * messagesForPhase_) {
        logger_.info("All OK finished. Phase finished.");
        phaseFinished_ = true;
        phaseFinished();
      }
      return null;
    }
  }

}
