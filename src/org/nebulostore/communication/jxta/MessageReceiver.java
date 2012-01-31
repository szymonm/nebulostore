package org.nebulostore.communication.jxta;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;

import net.jxta.impl.util.Base64;
import net.jxta.pipe.InputPipe;

import org.apache.log4j.Logger;

import org.nebulostore.appcore.Message;
import org.nebulostore.communication.messages.CommMessage;

/**
 * @author marcin
 */
class MessageReceiver implements Runnable {

  private final InputPipe inputPipe_;
  private final BlockingQueue<Message> outQueue_;

  private static Logger logger_ = Logger.getLogger(MessageReceiver.class);

  public MessageReceiver(InputPipe serverPipe, BlockingQueue<Message> outQueue) {
    inputPipe_ = serverPipe;
    outQueue_ = outQueue;
  }

  @Override
  public void run() {
    // TODO Auto-generated method stub
    while (true) {
      net.jxta.endpoint.Message m = null;
      try {
        m = inputPipe_.waitForMessage();
      } catch (InterruptedException t) {
        logger_.error("error in accept serverPipe", t);
        t.printStackTrace();
      }
      logger_.info("message received!");
      outQueue_.add(unwrapMessage(m));
      logger_.info("message on out queue");
    }
  }

  private Message unwrapMessage(net.jxta.endpoint.Message msg) {

    byte[] data = null;
    try {
      data = Base64
          .decodeBase64(msg.getMessageElement("serialized").toString());
    } catch (IOException e2) {
      // TODO Auto-generated catch block
      e2.printStackTrace();
    }
    logger_.info("message after decoding: " + msg.getMessageElement("serialized").toString());
    ByteArrayInputStream baos = new ByteArrayInputStream(data);
    ObjectInputStream ois = null;
    try {
      ois = new ObjectInputStream(baos);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    try {
      return (CommMessage) ois.readObject();
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      logger_.error("error:", e);
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      logger_.error("error:", e);
      e.printStackTrace();
    }
    return null;
  }
}
