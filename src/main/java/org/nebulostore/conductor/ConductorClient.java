package org.nebulostore.conductor;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.messages.ErrorMessage;
import org.nebulostore.conductor.messages.FinishMessage;
import org.nebulostore.conductor.messages.GatherStatsMessage;
import org.nebulostore.conductor.messages.InitMessage;
import org.nebulostore.conductor.messages.NewPhaseMessage;
import org.nebulostore.conductor.messages.StatsMessage;
import org.nebulostore.conductor.messages.TicMessage;
import org.nebulostore.conductor.messages.TocMessage;

/**
 * Base class for all TestingModules(test cases run on peers).
 * @author szymonmatejczyk
 *
 * Writing tests:
 *     1. Remember to set visitors for each phase.
 *     2. Don't forget to put phaseFinished in every visitor.
 *     3. By default you should define visitor for each phase. However, you can
 *        override getVisitor() method to use visitors differently(ex. more
 *        than once).
 *     4. Don't forget to write server(ServerTestingModule) that will initialize
 *        TestingModules on peers side and will be gathering results.
 */
public abstract class ConductorClient extends JobModule implements Serializable {
  private static final long serialVersionUID = -1686614265302231592L;
  private static Logger logger_ = Logger.getLogger(ConductorClient.class);

  protected final CommAddress server_;
  protected final String serverJobId_;
  protected final int numPhases_;

  protected int phase_;

  public ConductorClient(String serverJobId, int numPhases) {
    serverJobId_ = serverJobId;
    numPhases_ = numPhases;
    server_ = CommunicationPeer.getPeerAddress();
  }

  /**
   * Called after receiving Tic Message, which means that all peer have finished previous phase.
   */
  protected void advancedToNextPhase() {
    inQueue_.add(new NewPhaseMessage());
  }

  protected void phaseFinished() {
    logger_.debug("Phase finished. Sending TocMessage");
    networkQueue_.add(new TocMessage(serverJobId_, CommunicationPeer.getPeerAddress(), server_,
        phase_));
    ++phase_;
  }

  protected void endWithError(String message) {
    logger_.error(message);
    networkQueue_.add(new ErrorMessage(serverJobId_, null, server_, message));
    super.endModule();
  }

  /**
   * Visitors for phases. They are never send but initialized on clients side.
   */
  protected transient TestingModuleVisitor[] visitors_;

  /**
   * Get visitor for current phase. Null if bad phase (including one after the last, meaning
   * the test has ended for that peer).
   */
  private TestingModuleVisitor getVisitor() {
    if (visitors_ == null) {
      initVisitors();
    }
    logger_.debug("get Visitor in phase: " + phase_);
    if (visitors_ != null && phase_ < visitors_.length)
      return visitors_[phase_];
    else
      return null;
  }

  protected abstract void initVisitors();

  @Override
  protected void processMessage(Message message) throws NebuloException {
    logger_.debug("processMessage on: " + message.toString());
    TestingModuleVisitor visitor = getVisitor();
    if (visitor != null)
      message.accept(visitor);
    else
      logger_.debug("ignoring " + message.getClass().getName() + " in last phase.");
  }

  protected void assertTrue(Boolean b, String message) {
    if (!b) {
      logger_.warn("Assertion failed: " + message);
      networkQueue_.add(new ErrorMessage(serverJobId_, null, server_, message));
    }
  }

  /**
   * Visitor handling Tic and FinishTest messages.
   * @author szymonmatejczyk
   */
  protected abstract class TestingModuleVisitor extends MessageVisitor<Void> {
    @Override
    public abstract Void visit(NewPhaseMessage message);

    @Override
    public Void visit(TicMessage message) {
      logger_.debug("TicMessage received. Current phase: " + phase_ + "; Server phase: " +
          message.getPhase());
      if (message.getPhase() == phase_) {
        logger_.debug("TicMessage - executing phase " + phase_);
        advancedToNextPhase();
      }
      return null;
    }

    @Override
    public Void visit(FinishMessage message) {
      logger_.info("Test finished by server.");
      endJobModule();
      return null;
    }
  }

  /**
   * Empty visitor for phase 0.
   * @author szymonmatejczyk
   */
  protected class EmptyInitializationVisitor extends TestingModuleVisitor {
    public EmptyInitializationVisitor() { }

    @Override
    public Void visit(InitMessage message) {
      jobId_ = message.getId();
      logger_.debug("Test client initialized: " + message.getHandler().getClass().toString());
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
   * @author szymonmatejczyk
   */
  protected class IgnoreNewPhaseVisitor extends TestingModuleVisitor {
    public IgnoreNewPhaseVisitor() { }

    @Override
    public Void visit(NewPhaseMessage message) {
      return null;
    }
  }

  /**
   * Default visitor for last phase. Handles GatherStatsMessage (and FinishMessage).
   * @author bolek
   */
  protected class LastPhaseVisitor extends TestingModuleVisitor {
    protected CaseStatistics stats_;

    public LastPhaseVisitor(CaseStatistics stats) {
      stats_ = stats;
    }

    @Override
    public Void visit(GatherStatsMessage message) {
      logger_.debug("Sending statistics to server.");
      networkQueue_.add(new StatsMessage(serverJobId_, null, server_, stats_));
      return null;
    }

    @Override
    public Void visit(NewPhaseMessage message) {
      logger_.debug("Received NewPhaseMessage in GatherStats state.");
      return null;
    }
  }
}
