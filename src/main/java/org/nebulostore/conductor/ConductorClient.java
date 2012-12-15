package org.nebulostore.conductor;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.messages.ErrorMessage;
import org.nebulostore.conductor.messages.FinishMessage;
import org.nebulostore.conductor.messages.InitMessage;
import org.nebulostore.conductor.messages.NewPhaseMessage;
import org.nebulostore.conductor.messages.TicAckMessage;
import org.nebulostore.conductor.messages.TicMessage;
import org.nebulostore.conductor.messages.TocAckMessage;
import org.nebulostore.conductor.messages.TocMessage;

/**
 * Base class for all TestingModules(test cases run on peers).
 * @author szymonmatejczyk
 *
 * Writting tests: 1. Remember to set visitors for each
 *         phase. 2. Don't forget to put phaseFinished in every visitor. 3. By
 *         default you should define visitor for each phase. However, you can
 *         override getVisitor() method to use visitors differently(ex. more
 *         than once). 4. Don't forget to write server(ServerTestingModule) that
 *         will initialize TestingModules on peers side and will be gathering
 *         results. Running tests: 1. Server: Set tests you want to run in
 *         TestingPeer.main(). Run TestingPeer as main function. 2. Run peers as
 *         normal peers. You need n-1 of them for n peers test, because server
 *         itself acts also as a client. See also: Ping, Pong, PingPongServer
 */
public abstract class ConductorClient extends JobModule implements Serializable {
  private static final long serialVersionUID = -1686614265302231592L;

  private static Logger logger_ = Logger.getLogger(ConductorClient.class);

  protected int phase_;

  protected final CommAddress server_;
  protected final String serverJobId_;

  private long tocSentTime_;

  private Timer checkPendingTocs_;

  public ConductorClient(String serverJobId) {
    super();
    serverJobId_ = serverJobId;
    server_ = CommunicationPeer.getPeerAddress();
    tocSentTime_ = -1;
  }

  /**
   * @author szymonmatejczyk
   */
  class CheckPendingTocs extends TimerTask {
    private static final int MAX_RETRIES = 5;
    long lastSeen_;
    private int retries_;


    public CheckPendingTocs() {
      lastSeen_ = -2;
      retries_ = 0;
    }

    @Override
    public void run() {
      if (tocSentTime_ != -1) {
        if (tocSentTime_ == lastSeen_) {
          if (retries_ < MAX_RETRIES) {
            logger_.info("Retrying to send TocMessage.");
            phaseFinished();
            retries_++;
          } else {
            logger_.info("Module sepuku!");
            abortTest();
          }

        } else {
          lastSeen_ = tocSentTime_;
        }
      } else {
        retries_ = 0;
      }
    }
  }

  public void abortTest() {
    logger_.info("Test finished by server.");
    endJobModule();
  }

  protected void advancedToNextPhase() {
    logger_.debug("Got NewPhaseMessage");
    inQueue_.add(new NewPhaseMessage());
  }

  protected void phaseFinished() {
    logger_.debug("Phase finished. Sending TocMessage");
    tocSentTime_  = System.currentTimeMillis();
    networkQueue_.add(new TocMessage(serverJobId_, CommunicationPeer
        .getPeerAddress(), server_, phase_));
  }

  @Override
  public void endModule() {
    if (checkPendingTocs_ != null) {
      tocSentTime_ = -1;
      checkPendingTocs_.cancel();
    }
    super.endModule();
  }

  /*
   * Visitors for phases. They are never send but initialized on clients side.
   */
  protected transient TestingModuleVisitor[] visitors_;

  private TestingModuleVisitor getVisitor() {
    if (visitors_ == null) {
      initVisitors();
    }
    logger_.debug("get Visitor in phase: " + phase_);
    return visitors_[phase_];
  }

  protected abstract void initVisitors();

  @Override
  protected void processMessage(Message message) throws NebuloException {
    logger_.debug("processMessage on: " + message.toString());
    message.accept(getVisitor());
  }

  /**
   * Visitor handling Tic and FinishTest messages.
   *
   * @author szymonmatejczyk
   */
  protected abstract class TestingModuleVisitor extends MessageVisitor<Void> {
    @Override
    public abstract Void visit(NewPhaseMessage message);

    @Override
    public Void visit(TocAckMessage message) {
      if (message.getPhase() == phase_) {
        tocSentTime_ = -1;
      }
      return null;
    }

    @Override
    public Void visit(TicMessage message) {

      logger_.debug("TicMessage received. Curr phase: " + phase_ +
          " server phase: " + message.getPhase());
      if (!(message.getPhase() - phase_ <= 1)) {
        return null;
      }
      logger_.debug("TicMessage  - moving on with processing.");

      tocSentTime_ = -1;
      // Sending TicAck
      networkQueue_.add(new TicAckMessage(serverJobId_, CommunicationPeer
          .getPeerAddress(), server_, message.getPhase()));

      int oldPhase = phase_;
      phase_ = message.getPhase();
      if (oldPhase != phase_) {
        logger_.debug("Advancing to the next phase");
        advancedToNextPhase();
      }
      return null;
    }

    @Override
    public Void visit(FinishMessage message) {
      abortTest();
      return null;
    }
  }

  protected void assertTrue(Boolean b, String message) {
    if (!b) {
      logger_.warn("Assertion failed: " + message);
      networkQueue_.add(new ErrorMessage(serverJobId_, null, server_,
          message));
    }
  }

  /**
   * Empty visitor for phase 0.
   *
   * @author szymonmatejczyk
   */
  protected class EmptyInitializationVisitor extends TestingModuleVisitor {
    public EmptyInitializationVisitor() {
    }

    @Override
    public Void visit(InitMessage message) {
      jobId_ = message.getId();
      logger_.debug("Test client initialized: " +
          message.getHandler().getClass().toString());

      checkPendingTocs_ = new Timer();
      checkPendingTocs_.schedule(new CheckPendingTocs(), 2000, 2000);
      phaseFinished();
      return null;
    }

    @Override
    public Void visit(NewPhaseMessage message) {
      return null;
    }
  }

  /**
   * Visitor that ignores NewPhaseMessage.
   *
   * @author szymonmatejczyk
   */
  protected class IgnoreNewPhaseVisitor extends TestingModuleVisitor {
    @Override
    public Void visit(NewPhaseMessage message) {
      return null;
    }
  }
}