package pl.edu.uw.mimuw.nebulostore.appcore;

import java.util.concurrent.BlockingQueue;

public abstract class Module implements IModule, Runnable {
	
	protected BlockingQueue<Message> inQueue;
	protected BlockingQueue<Message> outQueue;

	public Module(BlockingQueue<Message> inQueue,
			BlockingQueue<Message> outQueue) {
		
		this.outQueue = outQueue;
		this.inQueue = inQueue;
	}
	
	@Override
	public void run() {
		
		while (true)
		{			
			try {
				processMessage(inQueue.take());			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	

}
