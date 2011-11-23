/*
 * DiscoveryServer
 * NebuloStore Communication Module
 * 
 * (c) ....
 * license info...
 */

package pl.edu.uw.mimuw.nebulostore.communication;

import java.io.File;
import java.util.Enumeration;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;

/**
 * @author Marcin Walas
 */
public class DiscoveryServer implements DiscoveryListener {

  private transient NetworkManager manager_;
  private transient DiscoveryService discovery_;

  /**
   * Constructor for the DiscoveryServer.
   */
  public DiscoveryServer() {
    try {
      manager_ = new NetworkManager(NetworkManager.ConfigMode.ADHOC,
          "DiscoveryServer",
          new File(new File(".cache"), "DiscoveryServer").toURI());
      manager_.startNetwork();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
    PeerGroup netPeerGroup = manager_.getNetPeerGroup();

    // get the discovery service
    discovery_ = netPeerGroup.getDiscoveryService();
  }

  /**
   * main
   * 
   * @param args
   *          command line args
   */

  /**
   * create a new pipe adv, publish it for 2 minut network time, sleep for 3
   * minutes, then repeat.
   */
  public void start() {
    long lifetime = 60 * 2 * 1000L;
    long expiration = 60 * 2 * 1000L;
    long waittime = 60 * 3 * 1000L;

    try {
      while (true) {
        PipeAdvertisement pipeAdv = getPipeAdvertisement();

        // publish the advertisement with a lifetime of 2 mintutes
        System.out
            .println("Publishing the following advertisement with lifetime :"
                + lifetime + " expiration :" + expiration);
        System.out.println(pipeAdv.toString());
        discovery_.publish(pipeAdv, lifetime, expiration);
        discovery_.remotePublish(pipeAdv, expiration);
        try {
          System.out.println("Sleeping for :" + waittime);
          Thread.sleep(waittime);
        } catch (Exception e) {
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * This method is called whenever a discovery response is received, which are
   * either in response to a query we sent, or a remote publish by another node.
   * 
   * @param ev
   *          the discovery event
   */
  @Override
  public void discoveryEvent(DiscoveryEvent ev) {

    DiscoveryResponseMsg res = ev.getResponse();

    // let's get the responding peer's advertisement
    System.out.println(" [  Got a Discovery Response ["
        + res.getResponseCount() + " elements]  from peer : " + ev.getSource()
        + "  ]");

    Advertisement adv;
    Enumeration en = res.getAdvertisements();

    if (en != null) {
      while (en.hasMoreElements()) {
        adv = (Advertisement) en.nextElement();
        System.out.println(adv);
      }
    }
  }

  /**
   * Creates a pipe advertisement
   * 
   * @return a Pipe Advertisement
   */
  public static PipeAdvertisement getPipeAdvertisement() {
    PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
        .newAdvertisement(PipeAdvertisement.getAdvertisementType());

    advertisement.setPipeID(IDFactory
        .newPipeID(PeerGroupID.defaultNetPeerGroupID));
    advertisement.setType(PipeService.UnicastType);
    advertisement.setName("Discovery tutorial");
    return advertisement;
  }

  /**
   * Stops the platform
   */
  public void stop() {
    // Stop JXTA
    manager_.stopNetwork();
  }
}
