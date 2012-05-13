package org.nebulostore.testing;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.GlobalContext;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.ReturningJobModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.async.messages.NetworkContextChangedMessage;
import org.nebulostore.broker.NetworkContext;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.testing.messages.ErrorTestMessage;
import org.nebulostore.testing.messages.FinishTestMessage;
import org.nebulostore.testing.messages.GatherStatsMessage;
import org.nebulostore.testing.messages.TestStatsMessage;
import org.nebulostore.testing.messages.TicMessage;
import org.nebulostore.testing.messages.TocMessage;

/**
 * Testing module that acts as a Test Server.
 * 
 * @author szymonmatejczyk Remember to set lastPhase_ and peersNeeded_ in
 *         subclass.
 */
public abstract class ServerTestingModule extends ReturningJobModule<Void> {
  private static Logger logger_ = Logger.getLogger(ServerTestingModule.class);

  /**
   * Time(in secs) after which, if not successful, test is failed.
   */
  public final int timeout_;

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
   * CommAddresses of the received Tocs
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
  private final int lastPhase_;

  /**
   * Number of peers found to start handshake procedure.
   */
  private final int peersFound_;

  /**
   * Number of peers needed to perform this test.
   */
  private final int peersNeeded_;

  /**
   * Visitor state - either initializing peers or running the tests on constant
   * set of peers
   */
  enum TestingState {
    Initializing, Running
  };

  private TestingState testingState_;

  private final ServerTestingModuleVisitor visitor_;

  /**
   * Additional, meaningful description of the test. To be printed on test
   * completion.
   */
  private final String testDescription_;

  /**
   * keeps starting time of the test
   */
  private long startTime_;

  /**
   * If set to true, then before test completion statistics will be gathered
   * from TestClients. Each of the TestClients should have an additional visitor
   * for puropse of stats gathering
   */
  private final boolean gatherStats_;

  /**
   * Timer for watching maximum stage time
   */
  private final Timer checkPhaseTimer_;

  protected ServerTestingModule(int lastPhase, int peersFound, int peersNeeded,
      int timeout, int phaseTimeout, String clientsJobId, boolean gatherStats,
      String testDescription) {
    clientsJobId_ = clientsJobId;
    lastPhase_ = lastPhase;
    timeout_ = timeout;
    peersFound_ = peersFound;
    peersNeeded_ = peersNeeded;
    gatherStats_ = gatherStats;
    testDescription_ = testDescription;
    visitor_ = new ServerTestingModuleVisitor();
    startTime_ = System.currentTimeMillis();

    checkPhaseTimer_ = new Timer();
    checkPhaseTimer_.schedule(new PhaseTimeoutTimer(this), phaseTimeout * 1000,
        phaseTimeout * 1000);

  }

  protected ServerTestingModule(int lastPhase, int peersNeeded, int timeout,
      String clientsJobId, String testDescription) {
    this(lastPhase, peersNeeded, peersNeeded, timeout, timeout, clientsJobId,
        false, testDescription);
  }

  class PhaseTimeoutTimer extends TimerTask {

    private int lastSeenPhase_ = -1;
    private final ServerTestingModule testModule_;

    public PhaseTimeoutTimer(ServerTestingModule serverTestingModule) {
      testModule_ = serverTestingModule;
    }

    @Override
    public void run() {
      if (lastSeenPhase_ == phase_) {
        logger_.warn("Phase stalled too long. Finishing test server.");
        testModule_.endWithError(new NebuloException("Phase " + phase_ +
            " stalled to long"));
      }
      lastSeenPhase_ = phase_;
    }

  }

  /**
   * Sends messages to discovered peers to initialize test modules on their
   * side. See: TestInitMessage, PingPongServer.
   */
  public abstract void initClients();

  /**
   * Sends messages with configuration to initialized clients that respond with
   * Toc messages.
   */
  public abstract void configureClients();

  /**
   * Feeds concrete implementation with statistics gathered from test clients.
   */
  public abstract void feedStats(TestStatistics stats);

  /**
   * Returns additional statistics from the upper module
   */
  protected abstract String getAdditionalStats();

  /**
   * Visitor.
   * 
   * @author szymonmatejczyk
   * @author Marcin Walas
   */
  protected class ServerTestingModuleVisitor extends MessageVisitor<Void> {
    private NetworkContextChangedMessage notificationMessage_;
    private final NetworkContext context_ = NetworkContext.getInstance();

    @Override
    public Void visit(JobInitMessage message) {
      if (NetworkContext.getInstance().getKnownPeers().size() >= peersFound_) {
        logger_
        .debug("Enough peers in NetworkContext. Initializing clients...");
        clients_ = new HashSet<CommAddress>(NetworkContext.getInstance()
            .getKnownPeers());
        testingState_ = TestingState.Initializing;
        initClients();
      } else {
        /*
         * wait for enough peers to perform the test - start to listen for
         * NetworkContext changes.
         */
        GlobalContext.getInstance().setDispatcherQueue(outQueue_);
        notificationMessage_ = new NetworkContextChangedMessage(message.getId());
        context_.addContextChangeMessage(notificationMessage_);
        logger_.debug("Waiting for peer discovery.");
      }
      return null;
    }

    @Override
    public Void visit(NetworkContextChangedMessage message) {
      if (NetworkContext.getInstance().getKnownPeers().size() >= peersFound_) {
        logger_.debug("Enough peers found, initializing test.");
        /* stop listening for notifications */
        NetworkContext.getInstance().removeContextChangeMessage(
            notificationMessage_);
        clients_ = new HashSet<CommAddress>(NetworkContext.getInstance()
            .getKnownPeers());
        testingState_ = TestingState.Initializing;
        initClients();
      }
      return null;
    }

    @Override
    public Void visit(TocMessage message) {
      if (!clients_.contains(message.getSourceAddress()) ||
          message.getPhase() != phase_) {
        return null;
      }
      tocs_++;
      tocsAddresses_.add(message.getSourceAddress());
      HashSet<CommAddress> tmp = new HashSet<CommAddress>(clients_);
      tmp.removeAll(tocsAddresses_);
      logger_.debug("TocMessage received. Tocs: " + tocs_ + "(from: " +
          message.getSourceAddress() + ")");
      logger_.debug("Still waiting for: " + tmp.toString());

      if (testingState_ == TestingState.Initializing) {
        logger_.debug("In state initializing");
        if (tocs_ >= peersNeeded_) {
          clients_ = tocsAddresses_;
          logger_.debug("clients set modified to: " + clients_);
          logger_.info("Advancing to the next phase.");

          phase_++;

          advancePhase();
          configureClients();


          testingState_ = TestingState.Running;
          startTime_ = System.currentTimeMillis();
        }
      } else {
        logger_.debug("In state running");

        if (tocs_ >= peersNeeded_) {
          phase_++;

          if (phase_ <= lastPhase_) {
            logger_.debug("Advanced to phase: " + phase_);
            advancePhase();
          } else {
            if (successful_) {

              if (gatherStats_) {
                logger_.debug("Getting stats from test clients.");
                advancePhase();
                for (CommAddress address : clients_) {
                  networkQueue_.add(new GatherStatsMessage(clientsJobId_, null,
                      address));
                }
              } else {
                logger_.info("Test " + testDescription_ +
                    " successfull. After: \t" +
                    (System.currentTimeMillis() - startTime_) +
                    getAdditionalStats());
                endWithSuccess(null);
              }
            } else {
              logger_.info("Test " + testDescription_ + " failed. After: \t" +
                  (System.currentTimeMillis() - startTime_) +
                  getAdditionalStats());
              endWithSuccess(null);
            }

          }
        }
      }
      return null;
    }

    @Override
    public Void visit(TestStatsMessage message) {
      if (!clients_.contains(message.getSourceAddress())) {
        return null;
      }
      tocs_++;
      logger_.debug("Got stats message. (" + tocs_ + "/" + peersNeeded_ +
          ") from " + message.getSourceAddress());

      feedStats(message.getStats());
      if (tocs_ >= peersNeeded_) {
        logger_.info("Test " + testDescription_ + " successfull. After: \t" +
            (System.currentTimeMillis() - startTime_) + getAdditionalStats());
        endWithSuccess(null);
      }
      return null;
    }

    private void advancePhase() {
      tocs_ = 0;
      tocsAddresses_ = new HashSet<CommAddress>();
      for (CommAddress address : clients_) {
        networkQueue_.add(new TicMessage(clientsJobId_, null, address, phase_));
      }
    }

    @Override
    public Void visit(ErrorTestMessage message) {
      logger_.warn("Received error, test failed: " + message.getMessage());
      successful_ = false;
      return null;
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  public Void getResult() throws NebuloException {
    return getResult(timeout_);
  }

  @Override
  public void endModule() {
    logger_.info("Test terminating. Sending finish test messages.");
    checkPhaseTimer_.cancel();
    for (CommAddress address : clients_) {
      networkQueue_.add(new FinishTestMessage(clientsJobId_, null, address));
    }
    super.endModule();
  }
}
