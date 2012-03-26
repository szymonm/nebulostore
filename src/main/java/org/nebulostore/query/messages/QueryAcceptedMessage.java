package org.nebulostore.query.messages;

import org.nebulostore.appcore.JobModule;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.CommMessage;

public class QueryAcceptedMessage extends CommMessage {

  /**
   * 
   */
  private static final long serialVersionUID = -5757649567524645218L;

  public QueryAcceptedMessage(CommAddress sourceAddress, CommAddress destAddress) {
    super(sourceAddress, destAddress);
  }

  @Override
  public JobModule getHandler() {
    return null;
    // TODO(bolek): Change it into a more specific exception type.
  }
}
