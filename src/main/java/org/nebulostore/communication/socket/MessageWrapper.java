package org.nebulostore.communication.socket;

import org.nebulostore.communication.messages.CommMessage;

public class MessageWrapper implements Serializable {
  private CommMessage commMessage_;
  private boolean isAck = false; 
  private static transient int lastId = -1;
  private int id;
  private int ackId = -1;

  public MessageWrapper(CommMessage commMessage) {
    commMessage_ = commMessage;
    id = ++lastId;
    lastId = Math.max(-1, lastId);
  }

  /**
   * Create response message.
   */
  public MessageWrapper(MessageWrapper responseMessage) {
    commMessage_ = null;
    isAck = true;
    id = ++lastId;
    lastId = Math.max(-1, lastId);
  }

  public CommMessage getCommMessage() {
    return commMessage_;
  }

  public boolean isResponse(MessageWrapper originalMessage) {
    return isAck && (originalMessage.id == ackId);
  }

}
