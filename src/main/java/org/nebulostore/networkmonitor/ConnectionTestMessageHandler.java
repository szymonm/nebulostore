package org.nebulostore.networkmonitor;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.networkmonitor.messages.ConnectionTestMessage;
import org.nebulostore.networkmonitor.messages.ConnectionTestResponseMessage;


/**
 * Responses for ConnectionTestMessage.
 */
public class ConnectionTestMessageHandler extends JobModule {
  private final CTMVisitor visitor_ = new CTMVisitor();

  /**
   * Visitor.
   */
  protected class CTMVisitor extends MessageVisitor<Void> {
    public Void visit(ConnectionTestMessage message) {
      networkQueue_.add(new ConnectionTestResponseMessage(message.getId(), null,
          message.getSourceAddress()));
      return null;
    }
  }

  @Override
  protected void processMessage(Message message) throws NebuloException {
    message.accept(visitor_);
  }

}
