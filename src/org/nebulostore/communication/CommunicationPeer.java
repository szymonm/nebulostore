package org.nebulostore.communication;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.Module;
import org.nebulostore.communication.jxta.JXTAPeer;
import org.nebulostore.communication.jxtach.JXTAChPeer;
import org.nebulostore.communication.messages.CommMessage;
import org.nebulostore.communication.messages.MsgCommPeerFound;
import org.nebulostore.communication.messages.MsgCommSendData;

/**
 * @author Marcin Walas
 */
public class CommunicationPeer extends Module {

  private final JXTAPeer jxtaPeer_;
  private final JXTAChPeer jxtaChPeer_;

  private final BlockingQueue<Message> jxtaPeerInQueue_;
  private final BlockingQueue<Message> jxtaChPeerInQueue_;

  private static Logger logger_ = Logger.getLogger(CommunicationPeer.class);

  public CommunicationPeer(BlockingQueue<Message> inQueue,
      BlockingQueue<Message> outQueue) {
    super(inQueue, outQueue);

    // TODO MBW: Reading configuration

    jxtaPeerInQueue_ = new LinkedBlockingQueue<Message>();
    jxtaChPeerInQueue_ = new LinkedBlockingQueue<Message>();

    /*
     * Here all messages from DHT and jxtaPeer are being forwarded straight to
     * Dispatcher.
     */
    jxtaPeer_ = new JXTAPeer(jxtaPeerInQueue_, outQueue);
    jxtaChPeer_ = new JXTAChPeer(jxtaChPeerInQueue_, outQueue);

    new Thread(jxtaPeer_).start();
    new Thread(jxtaChPeer_).start();
  }

  @Override
  protected void processMessage(Message msg) {

    if (msg instanceof MsgCommPeerFound) {
      outQueue_.add(msg);
    }

    if (msg instanceof MsgCommSendData || msg instanceof CommMessage) {

      if (((CommMessage) msg).getDestinationAddress() == jxtaPeer_
          .getPeerAddress()) {
        logger_.debug("message forwarded to Dispatcher");
        outQueue_.add(msg);
      } else {
        logger_.debug("message forwarded to jxtaPeer");
        jxtaPeerInQueue_.add(msg);
      }

    }

  }
}
