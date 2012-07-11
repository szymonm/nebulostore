package org.nebulostore.communication.socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.nebulostore.appcore.Message;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.InetAddress;
import java.util.Map;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;
import org.nebulostore.appcore.Module;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.socket.MessageWrapper;

/**
 * Module for receiving CommMessages through UDP.
 * @author Grzegorz Milka
 */
public class ListenerService extends Module {
  private DatagramSocket datagramSocket_;
  private static final int commCliPort_ = 9987;
  private static final int MAX_PACKET_SIZE = 1 << 18;
  private static final int MAX_SEND_RETRIES = 2;
  private static Logger logger_ = Logger.getLogger(ListenerService.class);
  private Map<InetAddress, Integer> idMap; //TODO idMap usage implementation

  public ListenerService(BlockingQueue<Message> outQueue) 
    throws NebuloException {
    super(null, outQueue);
    try {
      datagramSocket_ = new DatagramSocket(commCliPort_);
    } catch (SocketException e) {
      logger_.error("Could not initialize listening socket due to " + 
          "SocketException: " + e);
      throw new NebuloException("Could not initialize listening socket " +
          " due to SocketException: " + e);
    }

    idMap = new LinkedHashMap<InetAddress, Integer>();
  }

  @Override
  public void run() {
    DatagramPacket datagramPacket_ = new DatagramPacket(
        new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);
    while(true) {
      try {
        datagramSocket_.receive(datagramPacket_);
      } catch (IOException e) {
        logger_.error("Error when receiving packet: " + e);
        continue;
      }

      ByteArrayInputStream bais = 
        new ByteArrayInputStream(datagramPacket_.getData());
      Object readObject;
      try {
        readObject = (new ObjectInputStream(bais)).readObject();
      } catch (ClassNotFoundException e) {
        logger_.error("Error when receiving packet: " + e);
        continue;
      } catch (IOException e) {
        logger_.error("Error when receiving packet: " + e);
        continue;
      }

      MessageWrapper receivedMessage;
      try {
        receivedMessage = new MessageWrapper((CommMessage)readObject);
      } catch (ClassCastException e) {
        logger_.error("Error when casting received message " + e);
        continue;
      } 

      outQueue_.add(receivedMessage.getMessage());
      logger_.info("Added received message to outgoing Queue");

      // send acknowledgment
      MessageWrapper responseMessage = new MessageWrapper(receivedMessage);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        new ObjectOutputStream(baos).writeObject(receivedMessage);
      } catch (IOException e) {
        logger_.error("Error writing acknowledgment to bytes " + e);
        continue;
      }
      byte[] byteResponseMsg = baos.toByteArray();
      datagramPacket_.setData(byteResponseMsg, 0, byteResponseMsg.length);
      for(int retries = 0; retries < MAX_SEND_RETRIES; ++retries) {
        try {
          datagramSocket_.send(datagramPacket_);
          break;
        } catch (IOException e) {
          logger_.error("Couldn't send acknowledgment message " + e);
        }
      }
    }
  }

  @Override
  public void processMessage(Message msg) {
  }
}
