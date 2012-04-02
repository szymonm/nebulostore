package org.nebulostore.async.messages;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.MessageVisitor;
import org.nebulostore.appcore.exceptions.NebuloException;

/**
 * Message send to indicate that NetworkContext has changed.
 * @author szymonmatejczyk
 *
 */
public class NetworkContextChangedMessage extends Message {

  public NetworkContextChangedMessage(String jobID) {
    super(jobID);
  }

  @Override
  public <R> R accept(MessageVisitor<R> visitor) throws NebuloException {
    return visitor.visit(this);
  }
}
