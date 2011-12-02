package org.nebulostore.communication.messages;

/**
 * @author Marcin Walas
 */
public class MsgCommSendData extends CommMessage {

  public String address_;
  public String data_;

  public MsgCommSendData(String address, String data) {
    super(null, null);
    this.address_ = address;
    this.data_ = data;
  }
}
