package org.nebulostore.networkmonitor.messages;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.networkmonitor.ConnectionTestMessageHandler;

/**
 * Message send to test connection.
 */
public class ConnectionTestMessage extends CommMessage {
  private static final long serialVersionUID = 4478191855169810054L;

  public ConnectionTestMessage(CommAddress sourceAddress, CommAddress destAddress) {
    super(sourceAddress, destAddress);
  }

  @Override
  public JobModule getHandler() throws NebuloException {
    return new ConnectionTestMessageHandler();
  }


  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }


}
