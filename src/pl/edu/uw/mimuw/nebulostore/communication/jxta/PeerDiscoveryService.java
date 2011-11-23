package pl.edu.uw.mimuw.nebulostore.communication.jxta;

import net.jxta.discovery.DiscoveryService;
import net.jxta.protocol.PipeAdvertisement;

import org.apache.log4j.Logger;

/**
 * @author marcin
 */
class PeerDiscoveryService implements Runnable {

  private static Logger logger_ = Logger.getLogger(PeerDiscoveryService.class);
  private final DiscoveryService discovery_;

  private static long lifetime_ = 60 * 2 * 1000L;
  private static long expiration_ = 60 * 2 * 1000L;
  private static long waittime_ = 3 * 1000L;

  public PeerDiscoveryService(DiscoveryService discovery) {
    discovery_ = discovery;
  }

  @Override
  public void run() {
    logger_.info("Running!");

    discovery_.getRemoteAdvertisements(null, DiscoveryService.ADV, null, null,
        0, null);

    try {
      while (true) {
        logger_.info("Publishing advertisement");

        PipeAdvertisement pipeAdv = MessengerService.getPipeAdvertisement();

        // publish the advertisement with a lifetime of 2 mintutes
        logger_.info("Publishing the following advertisement with lifetime :"
            + lifetime_ + " expiration :" + expiration_);
        logger_.info(pipeAdv.toString());
        discovery_.publish(pipeAdv, lifetime_, expiration_);
        discovery_.remotePublish(pipeAdv, expiration_);
        try {
          logger_.info("Sleeping for :" + waittime_);
          Thread.sleep(waittime_);
        } catch (InterruptedException e) {
          logger_.error("", e);
        }

        // moving to receiving adv
        try {
          logger_.info("Sleeping for :" + waittime_);
          Thread.sleep(waittime_);
        } catch (InterruptedException e) {
          logger_.error("", e);
        }

        logger_.info("Sending a Discovery Message");
        discovery_.getRemoteAdvertisements(null, DiscoveryService.ADV, "Name",
            "Nebulostore messaging", 1, null);

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
