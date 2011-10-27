package pl.edu.uw.mimuw.nebulostore.appcore;

public abstract class Message extends net.jxta.endpoint.Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 110048388908555817L;
	
	/*
	 * To be used in merging queues mechanism
	 */
	public String QueueURI;

}
