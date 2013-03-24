package org.nebulostore.conductor;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.nebulostore.appcore.GlobalContext;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.ReturningJobModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.async.messages.NetworkContextChangedMessage;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.messages.ErrorMessage;
import org.nebulostore.conductor.messages.FinishMessage;
import org.nebulostore.conductor.messages.GatherStatsMessage;
import org.nebulostore.conductor.messages.StatsMessage;
import org.nebulostore.conductor.messages.TicMessage;
import org.nebulostore.conductor.messages.TocMessage;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.networkmonitor.NetworkContext;
import org.nebulostore.timer.MessageGenerator;

/**
 * Testing module that acts as a Test Server.
 * Remember to set lastPhase_ and peersNeeded_ in subclass.
 *
 * @author szymonmatejczyk
 */
public abstract class ConductorServer extends ReturningJobModule<Boolean> {
  private static Logger logger_ = Logger.getLogger(ConductorServer.class);
  private static final String N_PEERS_CONFIG = "systest.num-test-participants";

  /**
   * Time(in secs) after which, if not successful, test is failed.
   */
  public final int timeout_;
  public final int phaseTimeout_;

  /**
   * Current phase.
   */
  private int phase_;

  /**
   * Number of Tocs received from peers. After receiving TocMessage from each
   * peer, test advances to next phase.
   */
  private int tocs_;

  /**
   * CommAddresses of the received Tocs.
   */
  HashSet<CommAddress> tocsAddresses_ = new HashSet<CommAddress>();

  private boolean successful_ = true;

  /**
   * CommAdresses of peers performing this test.
   */
  protected HashSet<CommAddress> clients_;

  /**
   * JobId that clients use on their side.
   */
  protected final String clientsJobId_;

  /**
   * Last phase of the test. If test moves to next, it is completed
   * successfully.
   */
  protected final int lastPhase_;

  /**
   * Number of peers needed to perform this test.
   */
  protected int peersNeeded_;

  /**
   * Is server used also as one of test clients.
   */
  protected boolean useServerAsClient_ = true;

  /**
   * Are statistics gathered at the end of test (false by default).
   */
  protected boolean gatherStats_;

  /**
   * Visitor state - either initializing peers or running the tests on constant
   * set of peers.
   */
  enum TestingState {
    CollectingPeers, Initializing, Configuring, Running, GatheringStats
  };
  private TestingState testingState_;
  private final ServerTestingModuleVisitor visitor_;

  /**
   * Additional, meaningful description of the test. To be printed on test
   * completion.
   */
  private final String testDescription_;

  /**
   * keeps start/stop time of the test.
   */
  private long startTime_ = -1;
  private long stopTime_ = -1;

  /**
   * Timer for watching maximum stage time.
   */
  private final Timer internalCheckTimer_;

  protected ConductorServer(int lastPhase, int timeout, String clientsJobId,
      String testDescription) {
    this(lastPhase, 0, timeout, timeout, clientsJobId, testDescription);
  }

  protected ConductorServer(int lastPhase, int peersNeeded, int timeout,
      String clientsJobId, String testDescription) {
    this(lastPhase, peersNeeded, timeout, timeout, clientsJobId, testDescription);
  }

  protected ConductorServer(int lastPhase, int peersNeeded, int timeout, int phaseTimeout,
      String clientsJobId, String testDescription) {
    clientsJobId_ = clientsJobId;
    lastPhase_ = lastPhase;
    timeout_ = timeout;
    phaseTimeout_ = phaseTimeout;
    peersNeeded_ = peersNeeded;
    testDescription_ = testDescription;
    visitor_ = new ServerTestingModuleVisitor();
    internalCheckTimer_ = new Timer();
  }

  public void initialize(XMLConfiguration config) {
    schedulePhaseTimer();
    if (peersNeeded_ == 0)
      initializeFromConfig(config);
  }

  protected void schedulePhaseTimer() {
    internalCheckTimer_.schedule(new PhaseTimeoutTimer(),
        3L * 1000L * phaseTimeout_, 1000L * phaseTimeout_);
  }

  protected void initializeFromConfig(XMLConfiguration config) {
    peersNeeded_ = config.getInteger(N_PEERS_CONFIG, 0);
    if (peersNeeded_ == 0)
      throw new RuntimeException("Unable to initilize number of test participants from config!");
  }

  /**
   * Timer.
   */
  class PhaseTimeoutTimer extends TimerTask {
    private int lastSeenPhase_ = -1;

    @Override
    public void run() {
      if (lastSeenPhase_ == phase_) {
        logger_.warn("Phase stalled too long. Finishing test server.");
        successful_ = false;
        finishTest();
      }
      lastSeenPhase_ = phase_;
    }
  }

  /**
   * Called after enough clients are discovered.
   * Should send messages to discovered peers to initialize test modules on their side.
   */
  public abstract void initClients();

  /**
   * Called after the test is finished.
   * Should fill statistics gathered from test clients.
   */
  public abstract void feedStats(CommAddress sender, CaseStatistics stats);

  /**
   * Called after the test is finished. Should return additional statistics from the test.
   */
  protected abstract String getAdditionalStats();

  protected boolean isSuccessful() {
    return successful_;
  }

  private boolean trySetClients() {
    int needed = peersNeeded_ + (useServerAsClient_ ? 0 : 1);
    if (NetworkContext.getInstance().getKnownPeers().size() >= needed) {
      logger_.debug("Enough peers in NetworkContext. Initializing clients...");
      clients_ = new HashSet<CommAddress>(NetworkContext.getInstance().getKnownPeers());
      if (!useServerAsClient_) {
        clients_.remove(CommunicationPeer.getPeerAddress());
      }
      return true;
    } else {
      return false;
    }
  }

  // Send FinishMessage to everyone and die.
  private void finishTest() {
    logger_.info("Sending finish test messages.");
    internalCheckTimer_.cancel();
    if (clients_ != null) {
      for (CommAddress address : clients_) {
        networkQueue_.add(new FinishMessage(clientsJobId_, null, address));
      }
    }
    stopTime_ = System.currentTimeMillis();
    if (isSuccessful()) {
      logger_.info("Test " + testDescription_ + " successfull after: " +
          (stopTime_ - startTime_) + " ms " + getAdditionalStats());
      endWithSuccess(null);
    } else {
      logger_.info("Test " + testDescription_ + " failed. After: " +
          (stopTime_ - startTime_) + " ms " + getAdditionalStats());
      endWithError(new NebuloException("Unsuccessful test"));
    }
  }

  /**
   * Visitor.
   * @author szymonmatejczyk
   * @author Marcin Walas
   */
  protected class ServerTestingModuleVisitor extends MessageVisitor<Void> {
    private MessageGenerator notificationGenerator_;
    private final NetworkContext context_ = NetworkContext.getInstance();

    @Override
    public Void visit(JobInitMessage message) {
      logger_.debug("Received JobInitMessage.");
      jobId_ = message.getId();
      testingState_ = TestingState.CollectingPeers;
      if (trySetClients()) {
        testingState_ = TestingState.Initializing;
        initClients();
      } else {
        /*
         * wait for enough peers to perform the test - start to listen for
         * NetworkContext changes.
         */
        GlobalContext.getInstance().setDispatcherQueue(outQueue_);
        notificationGenerator_ = new MessageGenerator() {
          @Override
          public Message generate() {
            return new NetworkContextChangedMessage(getJobId());
          }
        };
        context_.addContextChangeMessageGenerator(notificationGenerator_);
        logger_.debug("Waiting for peer discovery.");
      }
      return null;
    }

    @Override
    public Void visit(NetworkContextChangedMessage message) {
      logger_.debug("Received NetworkContextChangedMessage.");
      if (testingState_ == TestingState.CollectingPeers && trySetClients()) {
        testingState_ = TestingState.Initializing;
        initClients();
      }
      return null;
    }

    @Override
    public Void visit(TocMessage message) {
      logger_.debug("Received TocMessage.");
      if (!clients_.contains(message.getSourceAddress()) || message.getPhase() != phase_) {
        return null;
      }

      logger_.debug("TocMessage received. Tocs: " + tocs_ + "(from: " +
          message.getSourceAddress() + ")");

      if (tocsAddresses_.contains(message.getSourceAddress())) {
        logger_.debug("Already received toc from this address");
        return null;
      }

      synchronized (tocsAddresses_) {
        tocs_++;
        tocsAddresses_.add(message.getSourceAddress());
        logger_.debug("Tocs incremented to: " + tocs_);

        if (tocs_ < peersNeeded_) {
          HashSet<CommAddress> tmp = new HashSet<CommAddress>(clients_);
          tmp.removeAll(tocsAddresses_);
          logger_.debug("Still waiting for " + (peersNeeded_ - tocs_) + " peers from: " +
              tmp.toString());
        }
        processTocsChange();
      }
      return null;
    }

    @Override
    public Void visit(StatsMessage message) {
      logger_.debug("Received StatsMessage.");
      if (!clients_.contains(message.getSourceAddress())) {
        return null;
      }
      tocs_++;
      logger_.debug("Got stats message. (" + tocs_ + "/" + peersNeeded_ + ") from " +
          message.getSourceAddress());

      feedStats(message.getSourceAddress(), message.getStats());
      if (tocs_ >= peersNeeded_) {
        finishTest();
      }
      return null;
    }

    @Override
    public Void visit(ErrorMessage message) {
      logger_.warn("Received ErrorMessage, test failed: " + message.getMessage());
      successful_ = false;
      return null;
    }
  }

  private void advancePhase() {
    ++phase_;
    tocs_ = 0;
    tocsAddresses_ = new HashSet<CommAddress>();
    sendTics();
  }

  private void processTocsChange() {
    if (testingState_ == TestingState.Initializing) {
      logger_.debug("In state initializing");
      if (tocs_ >= peersNeeded_) {
        clients_ = tocsAddresses_;
        logger_.debug("clients set modified to: " + clients_);
        logger_.info("Advancing to phase: " + (phase_ + 1));
        startTime_ = System.currentTimeMillis();
        advancePhase();
        testingState_ = TestingState.Running;
      }
    } else if (testingState_ == TestingState.Running) {
      logger_.debug("In state running phase: " + phase_);
      if (tocs_ >= peersNeeded_) {
        if ((phase_ + 1) <= lastPhase_) {
          logger_.info("Advancing to phase: " + (phase_ + 1));
          advancePhase();
        } else {
          ++phase_;
          if (gatherStats_) {
            logger_.info("Advancing to phase: " + phase_ + " (gathering stats)");
            testingState_ = TestingState.GatheringStats;
            tocs_ = 0;
            sendGatherStats();
          } else {
            finishTest();
          }
        }
      }
    }
  }

  private void sendTics() {
    logger_.debug("Sending Tic messages...");
    for (CommAddress address : clients_)
      networkQueue_.add(new TicMessage(clientsJobId_, null, address, phase_));
  }

  private void sendGatherStats() {
    logger_.debug("Getting stats from test clients...");
    for (CommAddress address : clients_)
      networkQueue_.add(new GatherStatsMessage(clientsJobId_, null, address));
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  public Boolean getResult() throws NebuloException {
    return getResult(timeout_);
  }
}
