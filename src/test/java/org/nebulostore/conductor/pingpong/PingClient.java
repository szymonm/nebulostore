package org.nebulostore.conductor.pingpong;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.pingpong.PingMessage;
import org.nebulostore.communication.messages.pingpong.PongMessage;
import org.nebulostore.conductor.ConductorClient;
import org.nebulostore.conductor.messages.NewPhaseMessage;

/**
 * Simple ping-pong test implementation.
 * @author szymonmatejczyk
 */
public class PingClient extends ConductorClient implements Serializable {
  private static final long serialVersionUID = 8676871234510749533L;

  private static Logger logger_ = Logger.getLogger(PingClient.class);

  private final CommAddress pongAddress_;

  public PingClient(String serverJobId, CommAddress pongAddress) {
    super(serverJobId);
    pongAddress_ = pongAddress;
  }

  @Override
  protected void initVisitors() {
    visitors_ =  new TestingModuleVisitor[3];
    visitors_[0] = new EmptyInitializationVisitor();
    visitors_[1] = new Visitor1();
    visitors_[2] = new Visitor2();
  }

  /**
   * Visitor - phase 1.
   * @author szymonmatejczyk
   */
  final class Visitor1 extends TestingModuleVisitor {
    public Void visit(NewPhaseMessage message) {
      networkQueue_.add(new PingMessage(jobId_, pongAddress_, 0));
      phaseFinished();
      return null;
    }
  }

  /**
   * Visitor - phase 2.
   * @author szymonmatejczyk
   */
  final class Visitor2 extends IgnoreNewPhaseVisitor {
    @Override
    public Void visit(PongMessage message) {
      logger_.debug("Received PongMessage.");
      assertTrue(message.getNumber() == 1, "Correct number received.");
      phaseFinished();
      return null;
    }
  }
}
