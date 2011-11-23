package pl.edu.uw.mimuw.nebulostore.communication.messages.dht;

import pl.edu.uw.mimuw.nebulostore.communication.address.CommAddress;
import pl.edu.uw.mimuw.nebulostore.communication.messages.CommMessage;

/**
 * @author marcin
 */
public class OkDHTMessage extends CommMessage {

  public OkDHTMessage(CommAddress sourceAddress, CommAddress destAddress) {
    super(sourceAddress, destAddress);
    // TODO Auto-generated constructor stub
  }

}
