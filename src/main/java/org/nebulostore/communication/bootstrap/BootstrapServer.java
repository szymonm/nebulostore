package org.nebulostore.communication.bootstrap;

import java.io.Serializable;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

/**
 * Simple UDP Bootstrap Server. 
 * BootstrapServer maintains a collection of all hosts in the nebulostore
 * network and serves this list to all joining hosts. 
 * @author Grzegorz Milka
 */
public class BootstrapServer implements Serializable, Runnable {
  private static final int KEEP_ALIVE = 90; // Time till a client is considered
                                            // dead.
  private static final int MAX_PACKET_SIZE = 1 << 12; // TODO: Is it enough
                                                      // to send serialized 
                                                      // CommMessage?
  private int servPort_ = 9988; // Time till a client is considered dead.
  private DatagramSocket serverSocket_;
  // TODO add some way to remember hosts and their age

  public BootstrapServer() throws IOException {
    serverSocket_ = new DatagramSocket(servPort_);
  }

  public BootstrapServer(int servPort) throws IOException {
    servPort_ = servPort;
    serverSocket_ = new DatagramSocket(servPort_);
  }

  void run() {
    // TODO listen for messages and send responses
    
    //DatagramPacket recvBuf = new DatagramPacket(new byte[MAX_PACKET_SIZE],
    //                                                     MAX_PACKET_SIZE)
    //while(true) {
    //  serverSocket_.receive(recvBuf);
    //  BootMessage ObjectInputStream(new ByteInputStream)
    //  recvBuf.setLength(MAX_PACKET_SIZE);
    //}
  }


}
