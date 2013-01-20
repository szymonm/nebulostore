package org.nebulostore.communication.streambinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.streambinding.ErrorStreamBindingMessage;
import org.nebulostore.communication.messages.streambinding.StreamBindingMessage;
import org.nebulostore.communication.messages.streambinding.StreamBindingReadyMessage;

/**
 * Test of StreamBinding protocol.
 *
 * @author Marcin Walas
 *
 */
public final class StreamBindingTest {

  private StreamBindingTest() {
  }

  public static void main(String[] args) {

    DOMConfigurator.configure("resources/conf/log4j.xml");

    Logger logger = Logger.getLogger(StreamBindingTest.class);

    BlockingQueue<Message> inQueue = new LinkedBlockingQueue<Message>();
    BlockingQueue<Message> outQueue = new LinkedBlockingQueue<Message>();

    CommunicationPeer communicationPeer = null;
    try {
      communicationPeer = new CommunicationPeer(inQueue, outQueue, null);
    } catch (NebuloException e) {
      e.printStackTrace();
      System.exit(-1);
    }

    new Thread(communicationPeer).start();

    // FOR boostrap process to take place
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    while (true) {

      Message msg = null;
      try {
        msg = outQueue.take();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      logger.debug("new message to process.. " + msg);

      if (msg != null) {
        logger.debug("msg is not null ");
        if (msg instanceof CommPeerFoundMessage) {
          inQueue.add(new StreamBindingMessage(((CommPeerFoundMessage) msg)
              .getSourceAddress(), ((CommPeerFoundMessage) msg)
              .getSourceAddress().toString()));
        }

        if (msg instanceof StreamBindingReadyMessage) {
          StreamBindingReadyMessage casted = (StreamBindingReadyMessage) msg;
          logger.info("stream binding ready message received with id: " + casted.getStreamId() +
              " isOutput:" + casted.isOutput());
          if (casted.isOutput()) {
            OutputStream stream = casted.getOutput();
            String message = "hmmm.. fajnie jest";
            try {
              stream.write(message.getBytes().length);
              stream.write(message.getBytes());

              logger.info("Written successfully to output stream.");
              logger.info("Closing...");
              stream.close();
            } catch (IOException e) {
              logger.error(e);
            }
          }

          if (casted.isInput()) {
            InputStream stream = casted.getInput();

            try {
              int len = stream.read();
              byte[] val = new byte[len];
              stream.read(val, 0, len);
              logger.info("Readed successfully message: " + new String(val));
              logger.info("Closing...");
              stream.close();

            } catch (IOException e) {
              logger.error(e);
            }

          }
        }

        if (msg instanceof ErrorStreamBindingMessage) {
          ErrorStreamBindingMessage casted = (ErrorStreamBindingMessage) msg;
          logger.error("RECEIVED ERROR MESSAGE", casted.getNetworkException());
        }

      }
    }
  }
}

