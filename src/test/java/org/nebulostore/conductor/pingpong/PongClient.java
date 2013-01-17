package org.nebulostore.conductor.pingpong;

import java.io.Serializable;
import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.conductor.ConductorClient;
import org.nebulostore.conductor.messages.NewPhaseMessage;
import org.nebulostore.conductor.messages.UserCommMessage;

/**
 * Pong.
 * @author szymonmatejczyk
 */
public class PongClient extends ConductorClient implements Serializable {
  private static Logger logger_ = Logger.getLogger(PongClient.class);
  private static final long serialVersionUID = -7238750658102427676L;

  BigInteger magicNumber_;
  CommAddress sender_;

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

  /**
   * Phase 1 - receive Ping.
   */
  final class Visitor1 extends IgnoreNewPhaseVisitor {
    @Override
    public Void visit(UserCommMessage message) {
      logger_.debug("Received PingMessage.");
      magicNumber_ = (BigInteger) message.getContent();
      sender_ = message.getSourceAddress();
      phaseFinished();
      return null;
    }
  }

  /**
   * Phase 2 - send Pong.
   */
  final class Visitor2 extends TestingModuleVisitor {
    @Override
    public Void visit(NewPhaseMessage message) {
      networkQueue_.add(new UserCommMessage(jobId_, sender_, magicNumber_.add(BigInteger.ONE)));
      phaseFinished();
      return null;
    }
  }
}
