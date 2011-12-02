package org.nebulostore.communication.tests;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import org.nebulostore.appcore.Message;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.messages.MsgCommPeerFound;
import org.nebulostore.communication.messages.pingpong.PingMessage;
import org.nebulostore.communication.messages.pingpong.PongMessage;

/**
 * @author Marcin Walas
 */
public class RunPingPong {

  /**
   * @param args
   */

  public static void main(String[] args) {

    DOMConfigurator.configure("log4j.xml");

    Logger logger = Logger.getLogger(RunPingPong.class);

    BlockingQueue<Message> inQueue = new LinkedBlockingQueue<Message>();
    BlockingQueue<Message> outQueue = new LinkedBlockingQueue<Message>();

    CommunicationPeer communicationPeer = new CommunicationPeer(inQueue,
        outQueue);
    new Thread(communicationPeer).start();

    while (true) {

      Message msg = null;
      try {
        msg = outQueue.take();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      if (msg != null) {
        if (msg instanceof MsgCommPeerFound) {
          logger.info("peer found!");
          inQueue.add(new PingMessage(((MsgCommPeerFound) msg)
              .getSourceAddress(), 0));
        }

        if (msg instanceof PingMessage) {

          PingMessage ping = (PingMessage) msg;
          logger.info("ping message received: " + ping.getNumber());
          inQueue.add(new PongMessage(ping.getSourceAddress(),
              ping.getNumber() + 1));
        }
        if (msg instanceof PongMessage) {

          PongMessage pong = (PongMessage) msg;
          logger.info("pong message received: " + pong.getNumber());
          inQueue.add(new PingMessage(pong.getSourceAddress(),
              pong.getNumber() + 1));
        }

      }

    }

  }
}
