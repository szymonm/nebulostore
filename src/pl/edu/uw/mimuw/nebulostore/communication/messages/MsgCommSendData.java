package pl.edu.uw.mimuw.nebulostore.communication.messages;

import pl.edu.uw.mimuw.nebulostore.appcore.Message;

public class MsgCommSendData extends Message {

	public String address;
	public String data;
	
	public MsgCommSendData(String address, String data)
	{
		this.address = address;
		this.data = data;
	}
}
