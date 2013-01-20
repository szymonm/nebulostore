package org.nebulostore.conductor;

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
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.messages.ErrorMessage;
import org.nebulostore.conductor.messages.FinishMessage;
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
   * Number of peers found to start handshake procedure.
   */
  protected final int peersFound_;

  /**
   * Number of peers needed to perform this test.
   */
  protected int peersNeeded_;

  /**
   * Is server used also as one of test clients.
   */
  protected boolean useServerAsClient_ = true;

  /**
   * Visitor state - either initializing peers or running the tests on constant
   * set of peers.
   */
  enum TestingState {
    Initializing, Configuring, Running, GatheringStats
  };
  private TestingState testingState_;
  private final ServerTestingModuleVisitor visitor_;

  /**
   * Additional, meaningful description of the test. To be printed on test
   * completion.
   */
  private final String testDescription_;

  /**
   * keeps starting time of the test.
   */
  private long startTime_ = -1;

  private long stopTime_ = -1;

  /**
   * If set to true, then before test completion statistics will be gathered
   * from TestClients. Each of the TestClients should have an additional visitor
   * for stats gathering
   */
  private final boolean gatherStats_;

  /**
   * Timer for watching maximum stage time.
   */
  private final Timer internalCheckTimer_;

  private long postponeStart_ = -1;
  private final long postponeDelay_ = 1;
  private final boolean postponeTics_ = true;

  private final int maximumLost_ = 1;
  private final int lostDelay_ = 7;
  private long firstToc_ = -1;

  protected ConductorServer(int lastPhase, int peersNeeded, int timeout,
      String clientsJobId, String testDescription) {
    this(lastPhase, peersNeeded, peersNeeded, timeout, timeout, clientsJobId,
        false, testDescription);
  }

  protected ConductorServer(int lastPhase, int peersFound, int peersNeeded,
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

    internalCheckTimer_ = new Timer();
    internalCheckTimer_.schedule(new PhaseTimeoutTimer(),
        3L * 1000L * phaseTimeout, 1000L * phaseTimeout);
    internalCheckTimer_.schedule(new PeriodicCheck(), 1000, 1000);
  }

  /**
   * @author szymonmatejczyk
   */
  class PeriodicCheck extends TimerTask {
    @Override
    public void run() {
      long time = System.currentTimeMillis();

      if (firstToc_ != -1) {
        if (((time - firstToc_) > lostDelay_ * 1000) && (peersNeeded_ - tocs_) < maximumLost_) {
          synchronized (tocsAddresses_) {
            clients_ = tocsAddresses_;
            logger_.info("Lost clients: " + (peersNeeded_ - tocs_));
            peersNeeded_ = tocs_;
            firstToc_ = -1;
            processTocsChange();
          }
        }
      }

      // logger_.debug("Checking for postponeTics_: " + postponeTics_ +
      // " postponeStart_: " + postponeStart_);
      if (postponeTics_ && postponeStart_ > 0) {
        logger_.debug("Postpone Tics checking whether to send time: " + time +
            " postponeStart_: " + postponeStart_ + " postponeDelay_: " +
            postponeDelay_);
        if (time - postponeStart_ > postponeDelay_ * 1000L) {
          sendTics();
          if (startTime_ > 0 && stopTime_ < 0) {
            logger_.debug("Elapsed milis: " + (time - startTime_));
            startTime_ += time - postponeStart_;
            logger_.debug("After reduction: " + (time - startTime_));
          }
          postponeStart_ = -1;
        }
      }
    }
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
        endWithError(new NebuloException("Phase " + phase_ + " stalled to long"));
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
  public abstract void feedStats(CaseStatistics stats);

  /**
   * Returns additional statistics from the upper module.
   */
  protected abstract String getAdditionalStats();


  protected boolean isSuccessful() {
    return true;
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
      jobId_ = message.getId();
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
      if (testingState_ != TestingState.Initializing && trySetClients()) {
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

      // Send ack
      logger_.debug("TocMessage received. Tocs: " + tocs_ + "(from: " +
          message.getSourceAddress() + ")");

      if (tocsAddresses_.contains(message.getSourceAddress())) {
        logger_.debug("Already received toc from this address");
        return null;
      }

      if (tocs_ == 0) {
        firstToc_ = System.currentTimeMillis();
      }

      synchronized (tocsAddresses_) {
        tocs_++;
        tocsAddresses_.add(message.getSourceAddress());
        logger_.debug("Tocs incremented to: " + tocs_);

        if (tocs_ >= peersNeeded_) {
          firstToc_ = -1;
        } else {
          HashSet<CommAddress> tmp = new HashSet<CommAddress>(clients_);
          tmp.removeAll(tocsAddresses_);
          logger_.debug("Still waiting for " + (peersNeeded_ - tocs_) + " peers from: " +
              tmp.toString());
        }

        processTocsChange();
      }

      return null;
    }

    //TODO(bolek): Send GatherStatsMessage.
    @Override
    public Void visit(StatsMessage message) {
      if (!clients_.contains(message.getSourceAddress())) {
        return null;
      }
      tocs_++;
      logger_.debug("Got stats message. (" + tocs_ + "/" + peersNeeded_ +
          ") from " + message.getSourceAddress());

      feedStats(message.getStats());
      if (tocs_ >= peersNeeded_) {
        if (isSuccessful()) {
          logger_.info("Test " + testDescription_ + " successfull. After: \t" +
              (stopTime_ - startTime_) + getAdditionalStats());
          endWithSuccess(null);
        } else {
          logger_.info("Test " + testDescription_ + " failed. After: \t" +
              (stopTime_ - startTime_) + getAdditionalStats());
          endWithError(new NebuloException("Not successful test"));
        }
      }
      return null;
    }


    @Override
    public Void visit(ErrorMessage message) {
      logger_.warn("Received error, test failed: " + message.getMessage());
      successful_ = false;
      return null;
    }
  }

  private void advancePhase() {
    tocs_ = 0;
    tocsAddresses_ = new HashSet<CommAddress>();
    if (postponeTics_) {
      postponeStart_ = System.currentTimeMillis();
    } else {
      sendTics();
    }
  }

  private void processTocsChange() {
    if (testingState_ == TestingState.Initializing) {
      logger_.debug("In state initializing");
      if (tocs_ >= peersNeeded_) {
        clients_ = tocsAddresses_;
        logger_.debug("clients set modified to: " + clients_);

        ++phase_;
        logger_.info("Advancing to phase: " + phase_);
        advancePhase();

        testingState_ = TestingState.Configuring;
      }
    } else if (testingState_ == TestingState.Configuring) {
      logger_.debug("In state configuring phase: " + phase_);
      if (tocs_ >= peersNeeded_) {
        phase_++;

        logger_.debug("Advanced to phase: " + phase_);
        testingState_ = TestingState.Running;
        startTime_ = System.currentTimeMillis();
        advancePhase();
      }
    } else if (testingState_ == TestingState.Running) {
      logger_.debug("In state running phase: " + phase_);

      if (tocs_ >= peersNeeded_) {
        phase_++;

        if (phase_ <= lastPhase_) {
          logger_.debug("Advanced to phase: " + phase_);
          advancePhase();
        } else {
          if (successful_) {

            if (gatherStats_) {
              logger_.debug("Getting stats from test clients...");
              stopTime_ = System.currentTimeMillis();
              testingState_ = TestingState.GatheringStats;
              advancePhase();

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
  }

  private void sendTics() {
    logger_.debug("Sending Tic messages...");
    for (CommAddress address : clients_)
      networkQueue_.add(new TicMessage(clientsJobId_, null, address, phase_));
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

  public Boolean getResult() throws NebuloException {
    return getResult(timeout_);
  }

  @Override
  public void endModule() {
    logger_.info("Test terminating. Sending finish test messages.");
    internalCheckTimer_.cancel();
    if (clients_ != null) {
      for (CommAddress address : clients_) {
        networkQueue_.add(new FinishMessage(clientsJobId_, null, address));
      }
    }
    super.endModule();
  }
}
