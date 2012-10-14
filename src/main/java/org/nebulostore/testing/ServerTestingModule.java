package org.nebulostore.testing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.GlobalContext;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.ReturningJobModule;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.async.messages.NetworkContextChangedMessage;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.dispatcher.messages.JobInitMessage;
import org.nebulostore.networkmonitor.NetworkContext;
import org.nebulostore.testing.messages.ErrorTestMessage;
import org.nebulostore.testing.messages.FinishTestMessage;
import org.nebulostore.testing.messages.GatherStatsMessage;
import org.nebulostore.testing.messages.TestStatsMessage;
import org.nebulostore.testing.messages.TicAckMessage;
import org.nebulostore.testing.messages.TicMessage;
import org.nebulostore.testing.messages.TocAckMessage;
import org.nebulostore.testing.messages.TocMessage;
import org.nebulostore.timer.IMessageGenerator;

/**
 * Testing module that acts as a Test Server.
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
   * for puropse of stats gathering
   */
  private final boolean gatherStats_;

  /**
   * Timer for watching maximum stage time.
   */
  private final Timer internalCheckTimer_;

  // TODO: Docs - move to ctor
  public Map<CommAddress, Long> pendingTicsAck_ = new HashMap<CommAddress, Long>();

  private long postponeStart_ = -1;
  private final long postponeDelay_ = 20;
  private final boolean postponeTics_ = true;

  private final  int maximumLost_ = 1;
  private final  int lostDelay_ = 7;
  private long firstToc_ = -1;

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

    internalCheckTimer_ = new Timer();
    internalCheckTimer_.schedule(new PhaseTimeoutTimer(),
        3 * phaseTimeout * 1000, phaseTimeout * 1000);
    internalCheckTimer_.schedule(new PeriodicCheck(), 1000, 1000);

  }

  protected ServerTestingModule(int lastPhase, int peersNeeded, int timeout,
      String clientsJobId, String testDescription) {
    this(lastPhase, peersNeeded, peersNeeded, timeout, timeout, clientsJobId,
        false, testDescription);
  }

  /**
   * @author szymonmatejczyk
   */
  class PeriodicCheck extends TimerTask {
    private static final long MAX_DELAY = 5000;

    @Override
    public void run() {
      long time = System.currentTimeMillis();

      synchronized (pendingTicsAck_) {
        for (CommAddress address : pendingTicsAck_.keySet()) {
          if ((time - pendingTicsAck_.get(address)) > MAX_DELAY) {
            logger_.debug("Sending again Tic to " + address);
            networkQueue_.add(new TicMessage(clientsJobId_, null, address,
                phase_));
            pendingTicsAck_.put(address, time);
          }
        }
      }

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
  public abstract void feedStats(TestStatistics stats);

  /**
   * Returns additional statistics from the upper module.
   */
  protected abstract String getAdditionalStats();


  protected boolean isSuccessful() {
    return true;
  }

  /**
   * Visitor.
   * @author szymonmatejczyk
   * @author Marcin Walas
   */
  protected class ServerTestingModuleVisitor extends MessageVisitor<Void> {
    private IMessageGenerator notificationGenerator_;
    private final NetworkContext context_ = NetworkContext.getInstance();

    @Override
    public Void visit(JobInitMessage message) {
      jobId_ = message.getId();
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
        notificationGenerator_ = new IMessageGenerator() {
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
      if (NetworkContext.getInstance().getKnownPeers().size() >= peersFound_) {
        logger_.debug("Enough peers found, initializing test.");
        /* stop listening for notifications */
        NetworkContext.getInstance().removeContextChangeMessageGenerator(notificationGenerator_);
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

      // Send ack
      networkQueue_.add(new TocAckMessage(clientsJobId_, null, message
          .getSourceAddress(), message.getPhase()));
      logger_.debug("TocMessage received. Tocs: " + tocs_ + "(from: " +
          message.getSourceAddress() + ") sending back Ack Message.");

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
        HashSet<CommAddress> tmp = new HashSet<CommAddress>(clients_);
        tmp.removeAll(tocsAddresses_);
        logger_.debug("TocMessage received. Tocs incremented to: " + tocs_);
        logger_.debug("Still waiting for: " + tmp.toString());

        if (tocs_ >= peersNeeded_) {
          firstToc_ = -1;
        }

        processTocsChange();
      }


      return null;
    }

    @Override
    public Void visit(TicAckMessage message) {

      synchronized (pendingTicsAck_) {
        pendingTicsAck_.remove(message.getSourceAddress());

        logger_.debug("Got Tic ack from " + message.getSourceAddress() +
            " still waiting for: " + pendingTicsAck_.size());

        if (pendingTicsAck_.size() == 0) {
          if (testingState_ == TestingState.Configuring) {
            configureClients();
          }
          if (testingState_ == TestingState.GatheringStats) {
            for (CommAddress address : clients_) {
              networkQueue_.add(new GatherStatsMessage(clientsJobId_, null,
                  address));
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
    public Void visit(ErrorTestMessage message) {
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
        logger_.info("Advancing to phase: " + phase_);

        phase_++;

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
    synchronized (pendingTicsAck_) {
      pendingTicsAck_ = new HashMap<CommAddress, Long>();
      for (CommAddress address : clients_) {
        networkQueue_.add(new TicMessage(clientsJobId_, null, address, phase_));
        long time = System.currentTimeMillis();
        pendingTicsAck_.put(address, time);
      }
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
    internalCheckTimer_.cancel();
    if (clients_ != null) {
      for (CommAddress address : clients_) {
        networkQueue_.add(new FinishTestMessage(clientsJobId_, null, address));
      }
    }
    super.endModule();
  }
}
