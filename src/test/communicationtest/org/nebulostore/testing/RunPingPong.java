package org.nebulostore.testing;

import java.util.Collection;
import java.util.Random;


import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.nebulostore.appcore.exceptions.NebuloException;
import org.nebulostore.testing.PingPongPeer;

/**
 * Executes given PingPongPeer and if it is set as server it tries 
 * to ping everyone and get responses.
 * @author Grzegorz Milka 
 */
public final class RunPingPong {
  private final static int PING_DELAY_ = 60000; //60 second
  private final static int BOOT_DELAY_ = 60000; //60 second
  /**
   * Wait time for client.
   */
  private final static int SLEEP_PERIOD_ = 600000; //10 minutes

  private final static Random randGenerator = new Random();

  public static void main(String[] args) throws NebuloException {
    DOMConfigurator.configure("resources/conf/log4j.xml");
    Logger logger = Logger.getLogger(RunPingPong.class);

    if(args.length < 2)
      throw new 
        IllegalArgumentException("Usage: program {PEER_ID} {server|client}");

    int peerId = Integer.parseInt(args[0]);
    boolean isServer = args[1].equals("server");

    PingPongPeer pingPongPeer = new PingPongPeer(peerId);
    if (isServer) {
      try{
        Thread.sleep(BOOT_DELAY_);
        int pingId = randGenerator.nextInt();
        pingPongPeer.sendPing(pingId);
        logger.info("Sent ping of id: " + pingId);
        Thread.sleep(PING_DELAY_);
        Collection<Integer> respondents = pingPongPeer.getRespondents(pingId);
        logger.info("Received response from: " + respondents);
      } catch (InterruptedException e) {
        logger.warn("Received interrupt. Ending server.");
        return;
      }
    } else {
      try{
        Thread.sleep(SLEEP_PERIOD_);
      } catch (InterruptedException e) {
        logger.warn("Received interrupt. Ending server.");
        return;
      }
    }
  }

}
