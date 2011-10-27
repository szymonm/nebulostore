package pl.edu.uw.mimuw.nebulostore.communication;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pl.edu.uw.mimuw.nebulostore.appcore.Message;
import pl.edu.uw.mimuw.nebulostore.appcore.Module;
import pl.edu.uw.mimuw.nebulostore.appcore.QueueMerger;

import pl.edu.uw.mimuw.nebulostore.communication.jxta.JXTAPeer;
import pl.edu.uw.mimuw.nebulostore.communication.jxtach.JXTAChPeer;
import pl.edu.uw.mimuw.nebulostore.communication.messages.MsgCommPeerFound;
import pl.edu.uw.mimuw.nebulostore.communication.messages.MsgCommSendData;

public class CommunicationPeer extends Module  {
	
	private JXTAPeer jxtaPeer;
	private JXTAChPeer jxtaChPeer;
	
	private QueueMerger queueMerger;
	
	private BlockingQueue<Message> mergedInQueue = new LinkedBlockingQueue<Message>();
	
	private BlockingQueue<Message> jxtaPeerIn;
	private BlockingQueue<Message> jxtaChPeerIn;
	private BlockingQueue<Message> jxtaPeerOut;	
	
	public CommunicationPeer(BlockingQueue<Message> inQueue, BlockingQueue<Message> outQueue) {
		super(null, outQueue);
		
		// TODO MBW: bad design - overwriting the queue in Module superclass
		this.inQueue = mergedInQueue;
		
		// TODO MBW: Reading configuration
		
		jxtaPeerIn = new LinkedBlockingQueue<Message>();
		jxtaPeerOut = new LinkedBlockingQueue<Message>();
		jxtaChPeerIn = new LinkedBlockingQueue<Message>();
		
		queueMerger = new QueueMerger(Arrays.asList(jxtaPeerOut, inQueue), mergedInQueue);
		new Thread(queueMerger).start();
		
		jxtaPeer = new JXTAPeer(jxtaPeerIn, jxtaPeerOut);
		jxtaChPeer = new JXTAChPeer(jxtaChPeerIn, jxtaPeerOut);		
		
		new Thread(jxtaPeer).start();
		new Thread(jxtaChPeer).start();
	}

	@Override
	public void processMessage(Message msg) {
		
		if (msg instanceof MsgCommPeerFound) {
			outQueue.add(msg);			
		}
		
		if (msg instanceof MsgCommSendData) {
			jxtaPeerIn.add(msg);			
		}
		
	}


}
