package org.nebulostore.testing;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.dispatcher.messages.JobEndedMessage;
import org.nebulostore.testing.messages.ErrorTestMessage;
import org.nebulostore.testing.messages.FinishTestMessage;
import org.nebulostore.testing.messages.NewPhaseMessage;
import org.nebulostore.testing.messages.TestInitMessage;
import org.nebulostore.testing.messages.TicMessage;
import org.nebulostore.testing.messages.TocMessage;

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
public abstract class TestingModule extends JobModule implements Serializable {
  private static final long serialVersionUID = -1686614265302231592L;

  private static Logger logger_ = Logger.getLogger(TestingModule.class);

  protected int phase_;

  protected final CommAddress server_;
  protected final String serverJobId_;

  public TestingModule(String serverJobId) {
    super();
    serverJobId_ = serverJobId;
    server_ = CommunicationPeer.getPeerAddress();
  }

  public void abortTest() {
    logger_.info("Test finished by server.");
    endJobModule();
    outQueue_.add(new JobEndedMessage(jobId_));
  }

  protected void advancedToNextPhase() {
    logger_.debug("Got NewPhaseMessage");
    inQueue_.add(new NewPhaseMessage());
  }

  protected void phaseFinished() {
    logger_.debug("Phase finished. Sending TocMessage");
    networkQueue_.add(new TocMessage(serverJobId_, CommunicationPeer
        .getPeerAddress(), server_, phase_));
  }

  /*
   * Visitors for phases. They are never send but initialized on clients side.
   */
  protected transient TestingModuleVisitor[] visitors_;

  private TestingModuleVisitor getVisitor() {
    if (visitors_ == null) {
      initVisitors();
    }
    logger_.debug("in phase: " + phase_);
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
    public Void visit(TicMessage message) {
      if (message.getPhase() - 1 != phase_) {
        return null;
      }
      phase_++;
      logger_.debug("TicMessage received. Advancing to the next phase");
      advancedToNextPhase();
      return null;
    }

    @Override
    public Void visit(FinishTestMessage message) {
      abortTest();
      return null;
    }
  }

  protected void assertTrue(Boolean b, String message) {
    if (!b) {
      logger_.warn("Assertion failed: " + message);
      networkQueue_.add(new ErrorTestMessage(serverJobId_, null, server_,
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
    public Void visit(TestInitMessage message) {
      jobId_ = message.getId();
      logger_.debug("Test client initialized: " +
          message.getHandler().getClass().toString());
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
