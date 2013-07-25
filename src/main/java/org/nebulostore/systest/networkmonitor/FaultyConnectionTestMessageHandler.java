package org.nebulostore.systest.networkmonitor;

import java.util.Random;

import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.appcore.messaging.Message;
import org.nebulostore.appcore.messaging.MessageVisitor;
import org.nebulostore.networkmonitor.ConnectionTestMessageHandler;
import org.nebulostore.networkmonitor.messages.ConnectionTestMessage;
import org.nebulostore.networkmonitor.messages.ConnectionTestResponseMessage;

/**
 * Module used in testing environment to response for ConnectionTestMessage with probability
 * responseFrequency_.
 *
 * @author szymonmatejczyk
 *
 */
public class FaultyConnectionTestMessageHandler extends ConnectionTestMessageHandler {
  private final double responseFrequency_;

  public FaultyConnectionTestMessageHandler(double responseFrequency) {
    responseFrequency_ = responseFrequency;
  }

  protected IncidentalCTMVisitor visitor_ = new IncidentalCTMVisitor();

  /**
   * Visitor.
   */
  public class IncidentalCTMVisitor extends MessageVisitor<Void> {
    public Void visit(ConnectionTestMessage message) {
      jobId_ = message.getId();
      Random r = new Random();
      if (r.nextDouble() < responseFrequency_) {
        networkQueue_.add(new ConnectionTestResponseMessage(message.getId(), message
            .getSourceAddress()));
      }
      endJobModule();
      return null;
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }
}
