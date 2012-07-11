package org.nebulostore.communication.jxta;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;

import net.jxta.pipe.InputPipe;

import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;
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
    while (true) {
      net.jxta.endpoint.Message m = null;
      try {
        m = inputPipe_.waitForMessage();
      } catch (InterruptedException t) {
        logger_.error("error in accept serverPipe", t);
        t.printStackTrace();
      }

      Message msg = unwrapMessage(m);
      if (msg != null) {
        logger_.debug("message received of type: " + msg.getClass());
        outQueue_.add(msg);
      }
    }
  }

  private Message unwrapMessage(net.jxta.endpoint.Message msg) {

    byte[] data = null;
    //    try {
    data = Base64.decode(msg.getMessageElement("serialized").toString());
    //    } catch (IOException e) {
    //      logger_.error(e);
    //      return null;
    //    }

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
      logger_.error(e);
    } catch (IOException e) {
      logger_.error(e);
    }
    return null;
  }
}
