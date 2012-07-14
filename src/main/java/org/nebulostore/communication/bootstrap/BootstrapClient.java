package org.nebulostore.communication.bootstrap;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.bootstrap.BootstrapMessage;
import org.nebulostore.communication.address.CommAddress;
import org.nebulostore.communication.messages.DiscoveryMessage;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import static org.nebulostore.communication.bootstrap.BootstrapMessageType.*;

/**
 * Simple UDP Bootstrap Client. 
 * BootstrapClient joins makes initial contact with BootstrapServer signaling
 * its entry to the nebulostore network and getting list of hosts present in the
 * network. 
 *
 * @author Grzegorz Milka
 */
public class BootstrapClient extends Module {
  private static Logger logger_ = Logger.getLogger(BootstrapClient.class);

  //TODO Is it correct address; move it to config file;
  private final String bootstrapServerAddress_ = "planetlab1.ci.pwr.wroc.pl";
  private final int bootstrapCliPort_ = 9989; 
  private final int bootstrapServPort_ = 9991; 
  private int commCliPort_ = 9987; 

  private static BootstrapMessage keepAliveMsg_;
  private static BootstrapMessage peerDiscoveryMsg_;

  private CommAddress myAddress_;

  private class PeerFoundListener implements Runnable {
    private ServerSocket serverSocket;
    public PeerFoundListener() throws IOException{
      serverSocket = new ServerSocket(bootstrapCliPort_);
    }
    public void run() {
      while(true) {
        try {
          Socket socket = serverSocket.accept();
          ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
          BootstrapMessage msg = (BootstrapMessage) ois.readObject();
          if(msg.getType() != PEER_INFO) {
            logger_.error("Received message different than PEER_INFO");
          }
          else {
            outQueue_.add(new CommPeerFoundMessage(msg.getPeerAddress(),
                  getPeerAddress()));
          }
          socket.close();
        } catch (IOException e) {
          logger_.error("Error when receiving peer info" + e);
          //TODO We might ask for resending peer info
        } catch (ClassNotFoundException e) {
          logger_.error("Error when receiving peer info" + e);
        }

      }
    }
  }

  public BootstrapClient(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue, int commCliPort) throws NebuloException {
    super(inQueue, outQueue);
    commCliPort_ = commCliPort;

    // Find my address
    logger_.info("Finding out my address.");
    Socket socket;
    InetAddress myAddress;
    try {
      socket = new Socket(bootstrapServerAddress_, bootstrapServPort_);
      myAddress = socket.getLocalAddress();
      logger_.debug("My address is: " + myAddress);
    } catch (IOException e) {
      logger_.error("Error during finding out peer address " + e);
      throw new NebuloException("Error during finding out peer address", e);
    }
    myAddress_ = new CommAddress(new InetSocketAddress(myAddress, commCliPort_));
    try {
      socket.close();
    } catch (IOException e) {
      logger_.error("Error when closing bootstrap socket " + e);
    }
    keepAliveMsg_ = new BootstrapMessage(KEEP_ALIVE, myAddress_);
    peerDiscoveryMsg_ = new BootstrapMessage(PEER_DISCOVERY, myAddress_);

    // Start listening for new peers
    Executor exec = Executors.newSingleThreadExecutor();
    try {
      exec.execute(new PeerFoundListener());
    } catch (IOException e) {
      logger_.error("Error when trying to initilize bootstrap listener " + e);
      throw new NebuloException("Error when trying to initilize bootstrap listener", e);
    }

    // Send hello keep alive
    while(true) {
      try {
        sendKeepAliveMsg();
        break;
      } catch (IOException e) {
        logger_.error("Error when sending hello message " + e);
      }
    }

    // Get all current peers
    while(true) {
      try {
        sendPeerDiscoveryMsg();
        break;
      } catch (IOException e) {
        logger_.error("Error when sending peer discovery message " + e);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException err) {
          // ignore
        }
      }
    }
  }

  @Override
  protected void processMessage(Message msg) {
    if (msg instanceof DiscoveryMessage) {
      while(true) {
        try {
          sendPeerDiscoveryMsg();
          break;
        } catch (IOException e) {
          logger_.error("Error when sending peer discovery message " + e);
        }
      }
    }
  }

  private void sendKeepAliveMsg() throws IOException {
    logger_.info("Sending KEEP_ALIVE to server.");
    Socket socket = new Socket(bootstrapServerAddress_, bootstrapServPort_);
    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
    oos.writeObject(keepAliveMsg_);
    socket.close();
  }

  private void sendPeerDiscoveryMsg() throws IOException {
    logger_.info("Sending PEER_DISCOVERY to server.");
    Socket socket = new Socket(bootstrapServerAddress_, bootstrapServPort_);
    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
    oos.writeObject(peerDiscoveryMsg_);
    socket.close();
  }

  /**
   * Returns this peer's address
   */
  public CommAddress getPeerAddress(){
    return myAddress_;
  }
}

