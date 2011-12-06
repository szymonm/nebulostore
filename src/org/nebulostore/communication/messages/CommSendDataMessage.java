package org.nebulostore.communication.messages;

/**
 * @author Marcin Walas
 */
public class CommSendDataMessage extends CommMessage {

  public String address_;
  public String data_;

  public CommSendDataMessage(String address, String data) {
    super(null, null);
    this.address_ = address;
    this.data_ = data;
  }
}
