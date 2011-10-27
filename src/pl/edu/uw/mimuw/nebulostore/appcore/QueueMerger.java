package pl.edu.uw.mimuw.nebulostore.appcore;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueMerger implements Runnable {
	
	
	private static long WAIT_CONST = 10;
	
	private List <BlockingQueue<Message>> inQueues;
	private BlockingQueue<Message> outQueue;
	
	public QueueMerger(List<BlockingQueue<Message>> inQueues, BlockingQueue<Message> outQueue)
	{
		this.inQueues = new LinkedList<BlockingQueue<Message>>(inQueues);
		this.outQueue = outQueue;
	}

	@Override
	public void run() {
		// TODO MBW: This is active waiting implementation - very bad...
		//      Bolek: rewrite it.
		while (true)
		{
			Message msg = null;
			for (BlockingQueue<Message> queue : inQueues)
			{
				try {
					msg = queue.poll(WAIT_CONST, TimeUnit.MILLISECONDS);					
				} catch (InterruptedException e) {
					// TODO MBW: I don't know what to do with this exception.
					//			 To be investigated.
					
					e.printStackTrace();
				}
				if (msg != null)
				{
					break;
				}
				
			}
			if (msg == null)
			{
				continue;
			}
			// TODO MBW: Marking source queue in msg 
			outQueue.add(msg);			
		}
		
	}

}
