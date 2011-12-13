package org.nebulostore.communication.tests;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.pingpong.PingMessage;
import org.nebulostore.communication.messages.pingpong.PongMessage;

/**
 * @author Marcin Walas
 */
public final class RunPingPong {

  private RunPingPong() { }

  /**
   * @param args
   */
  public static void main(String[] args) {

    DOMConfigurator.configure("resources/conf/log4j.xml");

    Logger logger = Logger.getLogger(RunPingPong.class);

    BlockingQueue<Message> inQueue = new LinkedBlockingQueue<Message>();
    BlockingQueue<Message> outQueue = new LinkedBlockingQueue<Message>();

    CommunicationPeer communicationPeer = null;
    try {
      communicationPeer = new CommunicationPeer(inQueue, outQueue);
    } catch (NebuloException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      System.exit(-1);
    }
    new Thread(communicationPeer).start();

    while (true) {

      Message msg = null;
      try {
        msg = outQueue.take();
      } catch (InterruptedException e) {
        logger.error("", e);
      }

      if (msg != null) {
        if (msg instanceof CommPeerFoundMessage) {
          logger.info("peer found!");
          inQueue.add(new PingMessage(((CommPeerFoundMessage) msg)
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
