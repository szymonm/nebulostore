package org.nebulostore.communication.socket;

import org.nebulostore.appcore.Message;
import org.nebulostore.communication.messages.CommMessage;
import java.io.Serializable;

public class MessageWrapper implements Serializable {
  private Message message_;
  private boolean isAck = false; 
  private static transient int lastId = -1;
  private int id;
  private int ackId = -1;

  public MessageWrapper(Message message) {
    message_ = message;
    id = ++lastId;
    lastId = Math.max(-1, lastId);
  }

  /**
   * Create response message.
   */
  public MessageWrapper(MessageWrapper responseMessage) {
    message_ = null;
    isAck = true;
    id = ++lastId;
    lastId = Math.max(-1, lastId);
  }

  public Message getMessage() {
    return message_;
  }

  public boolean isResponse(MessageWrapper originalMessage) {
    return isAck && (originalMessage.id == ackId);
  }

}
