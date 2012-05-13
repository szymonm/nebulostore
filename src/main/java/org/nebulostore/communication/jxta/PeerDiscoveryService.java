package org.nebulostore.communication.jxta;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.jxta.discovery.DiscoveryService;
import net.jxta.protocol.PipeAdvertisement;

import org.apache.log4j.Logger;

/**
 * @author marcin
 */
public class PeerDiscoveryService implements Runnable {

  private static Logger logger_ = Logger.getLogger(PeerDiscoveryService.class);
  private final DiscoveryService discoveryService_;

  private static long lifetime_ = 15 * 1000L;
  private static long expiration_ = 15 * 1000L;
  private static long waittime_ = 3000L;

  List<PipeAdvertisement> advertisements_ = new LinkedList<PipeAdvertisement>();

  public PeerDiscoveryService(DiscoveryService discoveryService) {
    discoveryService_ = discoveryService;
  }

  @Override
  public void run() {
    logger_.info("Running!");

    discoveryService_.getRemoteAdvertisements(null, DiscoveryService.ADV, null,
        null, 1, null);

    while (true) {
      logger_.info("Publishing advertisements");

      for (PipeAdvertisement adv : advertisements_) {
        logger_.info("Publishing advertisement with lifetime :" + lifetime_ +
            " expiration :" + expiration_);
        // logger_.info(adv.toString());
        try {
          discoveryService_.publish(adv, lifetime_, expiration_);
        } catch (IOException e) {
          logger_.error("", e);
        }
        discoveryService_.remotePublish(adv, expiration_);
      }

      try {
        Thread.sleep(waittime_);
      } catch (InterruptedException e) {
        logger_.error("", e);
      }

      logger_.info("Getting remove advertisements");
      discoveryService_.getRemoteAdvertisements(null, DiscoveryService.ADV,
          null, null, 10);
    }
  }

  public DiscoveryService getDiscoveryService() {
    return discoveryService_;
  }

  public void addAdvertisement(PipeAdvertisement adv) {
    advertisements_.add(adv);
  }

  public void removeAdvertisement(PipeAdvertisement advertisement) {
    advertisements_.remove(advertisement);
  }
}
