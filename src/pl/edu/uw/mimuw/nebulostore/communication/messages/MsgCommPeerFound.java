package pl.edu.uw.mimuw.nebulostore.communication.messages;

import pl.edu.uw.mimuw.nebulostore.appcore.Message;

public class MsgCommPeerFound extends Message {

	public MsgCommPeerFound(String address) {
		this.address = address;
	}

	public String address;
}
