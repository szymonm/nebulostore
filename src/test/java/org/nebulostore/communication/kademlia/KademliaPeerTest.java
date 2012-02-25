package org.nebulostore.communication.kademlia;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.appcore.Message;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.communication.CommunicationPeer;
import org.nebulostore.communication.dht.KeyDHT;
import org.nebulostore.communication.dht.ValueDHT;
import org.nebulostore.communication.messages.CommPeerFoundMessage;
import org.nebulostore.communication.messages.dht.GetDHTMessage;
import org.nebulostore.communication.messages.dht.OkDHTMessage;
import org.nebulostore.communication.messages.dht.OutDHTMessage;
import org.nebulostore.communication.messages.dht.PutDHTMessage;
import org.nebulostore.communication.messages.dht.ValueDHTMessage;
import org.nebulostore.communication.messages.pingpong.PingMessage;
import org.nebulostore.communication.messages.pingpong.PongMessage;

public class KademliaPeerTest {

  private KademliaPeerTest() {
  }

  public static void main(String[] args) {

    DOMConfigurator.configure("resources/conf/log4j.xml");

    Logger logger = Logger.getLogger(KademliaPeerTest.class);

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

    // FOR boostrap process to take place
    try {
      Thread.sleep(20000);
    } catch (InterruptedException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    while (true) {

      Message msg = null;
      try {
        msg = outQueue.take();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      logger.debug("new message to process.. " + msg);

      if (msg != null) {
        logger.debug("msg is not null ");
        if (msg instanceof CommPeerFoundMessage) {
          logger.info("peer found, getting its number...");
          inQueue.add(new PingMessage(((CommPeerFoundMessage) msg)
              .getSourceAddress(), peerNum));
        }

        if (msg instanceof PingMessage) {
          PingMessage ping = (PingMessage) msg;
          logger.info("ping message received with number: " + ping.getNumber());
          if (!foundPeers.contains(ping.getNumber())) {
            foundPeers.add(ping.getNumber());

          }
          inQueue.add(new PongMessage(ping.getSourceAddress(), peerNum));
        }
        if (msg instanceof PongMessage) {
          PongMessage pong = (PongMessage) msg;
          logger.info("pong message received with number: " + pong.getNumber());
          if (!foundPeers.contains(pong.getNumber())) {
            foundPeers.add(pong.getNumber());
          }
          logger.info("put with current number");
          inQueue.add(new PutDHTMessage("Kademlia test", new KeyDHT("" +
              peerNum), new ValueDHT("Hello World with finding: " +
              pong.getNumber())));
        }

        if (msg instanceof OutDHTMessage) {

          if (msg instanceof ValueDHTMessage) {
            logger.info("Received DHT response with: " +
                ((ValueDHTMessage) msg).getKey() + ":" +
                ((ValueDHTMessage) msg).getValue());
          }
          if (msg instanceof OkDHTMessage) {
            logger.info("Received OK DHT response with: " + msg.getId());
            logger
                .info("Sending get messages with keys to all found peers by now (foundPeers = " +
                    foundPeers + ")");
            try {
              Thread.sleep(10000);
            } catch (InterruptedException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
            for (int n : foundPeers) {
              inQueue.add(new GetDHTMessage("Bdbtest", new KeyDHT("" + n)));
            }
          }
        }

      }
    }
  }
}
