package org.nebulostore.communication.bootstrap;

import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.DatagramPacket;


/**
 * Simple UDP Bootstrap Client. 
 * BootstrapClient joins makes initial contact with BootstrapServer signaling
 * its entry to the nebulostore network and getting list of hosts present in the
 * network. 
 * It also periodically sends keep-alive meessage to the Server
 *
 * @author Grzegorz Milka
 */
public class BootstrapClient extends Module implements Serializable {
  //TODO Is it correct address; move it to config file;
  private final String bootstrapServerAddress_ = "roti.mimuw.edu.pl";
  private final int servPort_ = 9989; 
  private final KEEP_ALIVE_SEND_ = 30; // Period for sending KEEP ALIVE messages
  private final static DatagramPacket keepAlivePacket_ =
    new DatagramPacket(

  public BootstrapClient(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue) throws NebuloException {
    super(inQueue, outQueue);
    // TODO Run keep-alive sender periodical thread
    // TODO Run PEER_FOUND messages listener
  }

  @Override
  protected void processMessage(Message msg) {
    if (msg instanceof DiscoveryMessage) {
      // Send discovery Message
    }
  }
}
