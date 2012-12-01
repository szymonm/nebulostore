package org.nebulostore.communication.messages.performance;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.DataExchangeMessage;
import org.nebulostore.communication.messages.ReconfigureMessagesTestMessage;
import org.nebulostore.conductor.CaseStatistics;
import org.nebulostore.conductor.ConductorClient;
import org.nebulostore.conductor.messages.GatherStatsMessage;
import org.nebulostore.conductor.messages.NewPhaseMessage;
import org.nebulostore.conductor.messages.ReconfigurationMessage;
import org.nebulostore.conductor.messages.StatsMessage;
import org.nebulostore.conductor.messages.UserCommMessage;

/**
 */
public class PerformanceMessagesTestClient extends ConductorClient {

  private static Logger logger_ = Logger
      .getLogger(PerformanceMessagesTestClient.class);

  private static final long serialVersionUID = 2649769176662143910L;

  private final int messagesNumber_;
  private final int shortPhases_;
  private final long shortPhaseTimeout_;

  private CommAddress[] allClients_;
  private int expectedInClients_;

  private final CaseStatistics stats_;
  private Timer sendTimer_;

  public PerformanceMessagesTestClient(String serverJobId, int messagesNumber,
      int shortPhases, long shortPhaseTimeout) {
    super(serverJobId);
    messagesNumber_ = messagesNumber;
    shortPhases_ = shortPhases;
    shortPhaseTimeout_ = shortPhaseTimeout;

    stats_ = new CaseStatistics();
    stats_.setDouble("issued", 0.0);
    stats_.setDouble("received", 0.0);
    stats_.setDouble("shouldReceive", 0.0);
  }

  @Override
  protected void initVisitors() {
    sendTimer_ = new Timer();

    visitors_ = new TestingModuleVisitor[4];
    visitors_[0] = new EmptyInitializationVisitor();
    visitors_[1] = new ConfigurationVisitor();
    visitors_[2] = new MessagesVisitor();
    visitors_[3] = new StatisticsGatherVisitor();

  }

  /**
   */
  final class ConfigurationVisitor extends EmptyInitializationVisitor {

    @Override
    public Void visit(ReconfigurationMessage message) throws NebuloException {
      ReconfigureMessagesTestMessage rmtMessage;
      try {
        rmtMessage = (ReconfigureMessagesTestMessage) message;
      } catch (ClassCastException excpetion) {
        throw new NebuloException("Received wrong ReconfigurationMessage subclass!");
      }
      logger_.info("Got reconfiguration message with clients set: " +
          rmtMessage.getClients());
      allClients_ = rmtMessage.getClients().toArray(new CommAddress[0]);
      expectedInClients_ = rmtMessage.getExpectedInClients();

      stats_
      .setDouble("shouldReceive", 1.0 * expectedInClients_ * shortPhases_);
      phaseFinished();
      return null;
    }
  }

  /**
   */
  final class StatisticsGatherVisitor extends TestingModuleVisitor {

    @Override
    public Void visit(GatherStatsMessage message) {
      logger_.debug("Returning stats on request...");
      networkQueue_.add(new StatsMessage(serverJobId_, CommunicationPeer
          .getPeerAddress(), server_, stats_));
      return null;
    }

    @Override
    public Void visit(NewPhaseMessage message) {
      return null;
    }

  }

  /**
   */
  class SendTimer extends TimerTask {

    private final String payload_ = "Hello world";
    private int shortPhasesCount_;

    @Override
    public void run() {

      if (shortPhasesCount_ < shortPhases_) {
        logger_.info("Sending messages to remote hosts...");
        for (CommAddress destination : allClients_) {
          for (int i = 0; i < messagesNumber_; i++) {
            networkQueue_.add(new DataExchangeMessage(jobId_, CommunicationPeer
                .getPeerAddress(), destination, payload_, phase_, i));
          }
        }
        stats_.setDouble("issued", stats_.getDouble("issued") +
            new Double(allClients_.length * messagesNumber_));
        logger_.info("Sending messages to remote hosts. DONE.");
      } else {
        sendTimer_.cancel();
        phaseFinished();
      }

      shortPhasesCount_ += 1;

    }

  }

  /**
   */
  final class MessagesVisitor extends TestingModuleVisitor {

    @Override
    public Void visit(NewPhaseMessage message) {
      sendTimer_.schedule(new SendTimer(), shortPhaseTimeout_,
          shortPhaseTimeout_);
      return null;
    }

    @Override
    public Void visit(UserCommMessage message) throws NebuloException {
      if (message instanceof DataExchangeMessage) {
        stats_.setDouble("received", stats_.getDouble("received") + 1.0);
      } else {
        throw new NebuloException("Received wrong UserCommMessage subclass!");
      }
      return null;
    }
  }

}
