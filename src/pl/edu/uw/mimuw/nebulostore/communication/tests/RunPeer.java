package pl.edu.uw.mimuw.nebulostore.communication.tests;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pl.edu.uw.mimuw.nebulostore.appcore.Message;
import pl.edu.uw.mimuw.nebulostore.communication.CommunicationPeer;
import pl.edu.uw.mimuw.nebulostore.communication.messages.MsgCommPeerFound;
import pl.edu.uw.mimuw.nebulostore.communication.messages.MsgCommSendData;

public class RunPeer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BlockingQueue<Message> inQueue = new LinkedBlockingQueue<Message>();
		BlockingQueue<Message> outQueue  = new LinkedBlockingQueue<Message>();
		
		CommunicationPeer communicationPeer = new CommunicationPeer(inQueue, outQueue);
		new Thread(communicationPeer).start();
		
		while (true)
		{
			Message msg = null;
			try {				
				msg = outQueue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (msg != null)
			{
				if (msg instanceof MsgCommPeerFound)
				{
					inQueue.add(new MsgCommSendData(((MsgCommPeerFound)msg).address, "asdf"));
				}

			}
			
			
			
			
			
		}
		
		
		
	}

}
