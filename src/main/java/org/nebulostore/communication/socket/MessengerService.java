package org.nebulostore.communication.socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import org.nebulostore.appcore.Module;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.socket.MessageWrapper;

/**
 * Module for sending CommMessages through UDP.
 * It uses simple timeout-interlock mechanism for confirming delivery.
 * @author Grzegorz Milka
 */
private class MessengerService extends Module {
  private DatagramSocket datagramSocket_;
  private static final int NEBULOSTORE_PORT = 9991;
  private static final int TIMEOUT = 500;
  private static final int MAX_RETRIES = 3;
  private static Logger logger_ = Logger.getLogger(MessengerService.class);

  public MessengerService(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue) throws NebuloException {
    super(inQueue, outQueue);
    datagramSocket_ = new DatagramSocket();
  }

  @Override
  public void processMessage(Message msg) {
    try {
      datagramSocket_.setSoTimeout(TIMEOUT);
      processMessageRetry(msg, 0);
    } catch (Throwable t) {
      logger_.error("Serious error. Sleeping for 5 seconds...");
      t.printStackTrace();
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  // TODO parallelize receiving responses and sending retries or change to TCP
  // NOTE Remember that id in message wrappers loops and it might be a problem
  // in some extremely border cases.
  /**
   * Sends message with confirmation of receival in simple interlock mechanism.
   */
  private void processMessageRetry(Message msg, int retries) { 
    Exception lastError = null;
    MessageWrapper msgWrapper = new MessageWrapper(msg);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    new ObjectOutputStream(byteArrayOutputStream).writeObject(msgWrapper);
    byte[] byteMsg = byteArrayOutputStream.toByteArray();

    for(retries = 0; retries < MAX_RETRIES; ++retries) {
      if (((CommMessage) msg).getDestinationAddress() == null) {
        outQueue_.add(new ErrorCommMessage((CommMessage) msg,
              new CommException("Message " + msg.toString() +
                " with null destination address")));
        logger_.error("Message with null destination address");
        return;
      }

      CommAddress destAddress = ((CommMessage) msg).getDestinationAddress();

      // Send packet
      try {
        logger_.debug("Message to be sent over network to: " + destAddress);

        DatagramPacket datagramPacket_ = new DatagramPacket(byteMsg,
            byteMsg.length, destAddress.getAddress, NEBULOSTORE_PORT);
        datagramSocket_.send(datagramPacket);
        logger_.debug("sent to " + destAddress);
      } catch (IOException e) {
        logger_.error(e);
        lastError = e;
        continue;
      }

      // Receive acknowledgment
      try {
        DatagramPacket datagramPacket_ = new DatagramPacket(
            new byte[byteMsg.length], byteMsg.length);
        datagramSocket_.receive(datagramPacket);
        // TODO Check if source is from sender
        ByteArrayInputStream byteArrayInputStream = 
          new ByteArrayInputStream(datagramPacket.getData());

        Object readObj = 
          new ObjectInputStream(byteArrayInputStream).readObject();

        MessageWrapper receivedMessage = (MessageWrapper) readObj();
        if(receivedMessage.isResponse(msgWrapper)) {
          logger_.info("Received acknowledgment for message.");
          return;
        }
        else {
          logger_.error("Received response message which is not acknowledgment.");
        }

        // TODO More verbose error messages
      } catch (SocketTimeoutException e) {
        logger_.warning("Timed out getting response, retrying...");
        datagramSocket_.setSoTimeout(datagramSocket_.getSoTimeout() << 1);
        lastError = e;
      } catch (IOException e) {
        outQueue_.add(new ErrorCommMessage((CommMessage) msg, lastError));
        logger_.error("Error at receiveing acknowledgment: " + e);
        lastError = e;
      } catch (ClassNotFoundException e) {// |ClassCastException, assuming we 
                                          // might not be using javaSE 7
        outQueue_.add(new ErrorCommMessage((CommMessage) msg, lastError));
        logger_.error("Error at receiveing acknowledgment: " + e);
        lastError = e;
      } catch (ClassCastException e) {
        outQueue_.add(new ErrorCommMessage((CommMessage) msg, lastError));
        logger_.error("Error at receiveing acknowledgment: " + e);
        lastError = e;
      }
      

    }
    else {
      outQueue_.add(new ErrorCommMessage((CommMessage) msg, lastError));
      logger_.error("Max retries elapsed, raising error message...");
    }
  }
