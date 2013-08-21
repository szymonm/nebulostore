package org.nebulostore.systest.broker;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.ConductorClient;
import org.nebulostore.conductor.messages.GatherStatsMessage;
import org.nebulostore.conductor.messages.NewPhaseMessage;
import org.nebulostore.conductor.messages.StatsMessage;
import org.nebulostore.networkmonitor.ConnectionAttribute;
import org.nebulostore.networkmonitor.ConnectionTestMessageHandler;
import org.nebulostore.networkmonitor.GetPeersConnectionAverageStatisticModule;
import org.nebulostore.networkmonitor.PeerConnectionSurvey;
import org.nebulostore.systest.messages.ChangeTestMessageHandlerMessage;
import org.nebulostore.systest.networkmonitor.FaultyConnectionTestMessageHandler;
import org.nebulostore.timer.TimeoutMessage;
import org.nebulostore.timer.Timer;
import org.nebulostore.utils.Filter;

/**
 * Sets up ConnectionTestMesssageHandler and waits.
 * @author szymonmatejczyk
 */
public class BrokerTestClient extends ConductorClient {
  private static final long serialVersionUID = -7209499692156491320L;
  private static Logger logger_ = Logger.getLogger(BrokerTestClient.class);

  private static final int MONITORING_TIME = 25000;


  private static final int INITIAL_SLEEP = 4000;

  private long testStartTime_;

  /**
   * Availability simulated by this peer.
   */
  private final double availability_;

  /**
   * Clients known by this peer.
   */
  private final List<CommAddress> clients_;

  private Timer timer_;
  private CommAddress myAddress_;

  @Inject
  public void setDependencies(Timer timer, CommAddress commAddress) {
    timer_ = timer;
    myAddress_ = commAddress;
  }

  public BrokerTestClient(String serverJobId, int numPhases, CommAddress serverCommAddress,
      List<CommAddress> clients, double availability) {
    super(serverJobId, numPhases, serverCommAddress);
    availability_ = availability;
    clients_ = clients;
  }

  @Override
  protected void initVisitors() {
    // Setting NetworkMonitor to imitate failures.
    Provider<ConnectionTestMessageHandler> providerOfFaultyHandler =
        new Provider<ConnectionTestMessageHandler>() {
        @Override
        public ConnectionTestMessageHandler get() {
          return new FaultyConnectionTestMessageHandler(availability_);
        }
      };
    outQueue_.add(new ChangeTestMessageHandlerMessage(providerOfFaultyHandler));
    sleep(INITIAL_SLEEP);
    visitors_ = new TestingModuleVisitor[numPhases_ + 2];
    visitors_[0] = new EmptyInitializationVisitor();
    visitors_[1] = new DelayingVisitor(MONITORING_TIME);
    visitors_[2] = new NetworkMonitorLastPhaseVisitor();
  }

  /**
   * Visotors that waits @param timeout_ to finish phase.
   */
  protected class DelayingVisitor extends TestingModuleVisitor {
    protected int timeout_;

    public DelayingVisitor(int timeout) {
      timeout_ = timeout;
    }

    @Override
    public Void visit(NewPhaseMessage message) {
      logger_.debug("Phase delaying started...");
      testStartTime_ = System.currentTimeMillis();
      timer_.schedule(jobId_, timeout_);
      return null;
    }

    @Override
    public Void visit(TimeoutMessage message) {
      logger_.debug("Phase delaying finished.");
      phaseFinished();
      return null;
    }
  }

  /**
   * Sends statistics gathered from DHT to server.
   */
  protected class NetworkMonitorLastPhaseVisitor extends TestingModuleVisitor {
    @Override
    public Void visit(NewPhaseMessage message) {
      logger_.debug("Received NewPhaseMessage in GatherStats state.");
      return null;
    }

    public Void visit(GatherStatsMessage message) {
      logger_.debug("Gathering statistics...");
      if (gatherNetworkMonitorStatistics()) {
        networkQueue_.add(new StatsMessage(serverJobId_, null, server_, null));
      }
      return null;
    }

  }

   /**
   * Tries to gather statistics for known clients. Return true iff it succeeds.
   */
  private boolean gatherNetworkMonitorStatistics() {
    return true;
  }
}
