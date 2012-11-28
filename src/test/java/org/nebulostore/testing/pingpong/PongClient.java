package org.nebulostore.testing.pingpong;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.nebulostore.communication.messages.pingpong.PingMessage;
import org.nebulostore.communication.messages.pingpong.PongMessage;
import org.nebulostore.conductor.ConductorClient;
import org.nebulostore.conductor.messages.NewPhaseMessage;

/**
 * Pong.
 * @author szymonmatejczyk
 */
public class PongClient extends ConductorClient implements Serializable {
  private static Logger logger_ = Logger.getLogger(PongClient.class);

  private static final long serialVersionUID = -7238750658102427676L;

  public PongClient(String serverJobId) {
    super(serverJobId);
  }

  @Override
  protected void initVisitors() {
    visitors_ =  new TestingModuleVisitor[3];
    visitors_[0] = new EmptyInitializationVisitor();
    visitors_[1] = new Visitor1();
    visitors_[2] = new Visitor2();
  }

  PingMessage ping_;

  /**
   * Visitor.
   */
  final class Visitor1 extends IgnoreNewPhaseVisitor {
    @Override
    public Void visit(PingMessage message) {
      logger_.debug("Received PingMessage.");
      ping_ = message;
      phaseFinished();
      return null;
    }
  }

  /**
   * Visitor.
   */
  final class Visitor2 extends TestingModuleVisitor {
    @Override
    public Void visit(NewPhaseMessage message) {
      networkQueue_.add(new PongMessage(jobId_, ping_.getSourceAddress(), ping_.getNumber() + 1));
      phaseFinished();
      return null;
    }
  }
}
