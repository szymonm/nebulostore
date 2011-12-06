package org.nebulostore.communication.bdbdht.tests;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.messages.MsgCommPeerFound;
import org.nebulostore.communication.messages.pingpong.PingMessage;
import org.nebulostore.communication.messages.pingpong.PongMessage;

public class BdbPeerTest {

  public static void main(String[] args) {

    DOMConfigurator.configure("resources/conf/log4j.xml");

    Logger logger = Logger.getLogger(BdbPeerTest.class);

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

    int peerNum = Integer.parseInt(args[0]);
    List<Integer> foundPeers = new LinkedList<Integer>();

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
          logger.info("peer found, getting its number...");
          inQueue.add(new PingMessage(((MsgCommPeerFound) msg)
              .getSourceAddress(), peerNum));
        }

        if (msg instanceof PingMessage) {
          PingMessage ping = (PingMessage) msg;
          logger.info("ping message received: " + ping.getNumber());
          if (!foundPeers.contains(ping.getNumber())) {
            foundPeers.add(ping.getNumber());
          }
          inQueue.add(new PongMessage(ping.getSourceAddress(), peerNum));
        }

      }

    }
  }
}
